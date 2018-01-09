package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.init.DeviceSounds;
import com.mrcrayfish.device.util.CollisionHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.mrcrayfish.device.tileentity.TileEntityPrinter.State.*;

/**
 * Author: MrCrayfish
 */
public class TileEntityPrinter extends TileEntityNetworkDevice
{
    private State state = IDLE;

    private Deque<IPrint> printQueue = new ArrayDeque<>();
    private IPrint currentPrint;

    private int totalPrintTime;
    private int remainingPrintTime;
    private int paperCount = 0;

    @Override
    public void update()
    {
        if(!world.isRemote)
        {
            if(remainingPrintTime > 0)
            {
                if(remainingPrintTime % 20 == 0 || state == LOADING_PAPER)
                {
                    pipeline.setInteger("remainingPrintTime", remainingPrintTime);
                    sync();
                    if(remainingPrintTime != 0 && state == PRINTING)
                    {
                        world.playSound(null, pos, DeviceSounds.PRINTER_PRINTING, SoundCategory.BLOCKS, 0.5F, 1.0F);
                    }
                }
                remainingPrintTime--;
            }
            else
            {
                setState(state.next());
            }
        }

        if(state == IDLE && remainingPrintTime == 0 && currentPrint != null)
        {
            if(!world.isRemote)
            {
                IBlockState state = world.getBlockState(pos);
                double[] fixedPosition = CollisionHelper.fixRotation(state.getValue(BlockPrinter.FACING), 0.15, 0.5, 0.15, 0.5);
                EntityItem entity = new EntityItem(world, pos.getX() + fixedPosition[0], pos.getY() + 0.0625, pos.getZ() + fixedPosition[1], IPrint.generateItem(currentPrint));
                entity.motionX = 0;
                entity.motionY = 0;
                entity.motionZ = 0;
                world.spawnEntity(entity);
            }
            currentPrint = null;
        }

        if(state == IDLE && currentPrint == null && !printQueue.isEmpty() && paperCount > 0)
        {
            print(printQueue.poll());
        }
    }

    @Override
    public String getDeviceName()
    {
        return "Printer";
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("currentPrint", Constants.NBT.TAG_COMPOUND))
        {
            currentPrint = IPrint.loadFromTag(compound.getCompoundTag("currentPrint"));
        }
        if(compound.hasKey("totalPrintTime", Constants.NBT.TAG_INT))
        {
            totalPrintTime = compound.getInteger("totalPrintTime");
        }
        if(compound.hasKey("remainingPrintTime", Constants.NBT.TAG_INT))
        {
            remainingPrintTime = compound.getInteger("remainingPrintTime");
        }
        if(compound.hasKey("state", Constants.NBT.TAG_INT))
        {
            state = State.values()[compound.getInteger("state")];
        }
        if(compound.hasKey("paperCount", Constants.NBT.TAG_INT))
        {
            paperCount = compound.getInteger("paperCount");
        }
        if(compound.hasKey("queue", Constants.NBT.TAG_LIST))
        {
            printQueue.clear();
            NBTTagList queue = compound.getTagList("queue", Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < queue.tagCount(); i++)
            {
                IPrint print = IPrint.loadFromTag(queue.getCompoundTagAt(i));
                printQueue.offer(print);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("totalPrintTime", totalPrintTime);
        compound.setInteger("remainingPrintTime", remainingPrintTime);
        compound.setInteger("state", state.ordinal());
        compound.setInteger("paperCount", paperCount);
        if(currentPrint != null)
        {
            compound.setTag("currentPrint", IPrint.writeToTag(currentPrint));
        }
        if(!printQueue.isEmpty())
        {
            NBTTagList queue = new NBTTagList();
            printQueue.forEach(print -> {
                queue.appendTag(IPrint.writeToTag(print));
            });
            compound.setTag("queue", queue);
        }
        return compound;
    }

    @Override
    public NBTTagCompound writeSyncTag()
    {
        NBTTagCompound tag = super.writeSyncTag();
        tag.setInteger("paperCount", paperCount);
        return tag;
    }

    public void setState(State newState)
    {
        if(newState == null)
            return;

        state = newState;
        if(state == PRINTING)
        {
            if(DeviceConfig.isOverridePrintSpeed())
            {
                remainingPrintTime = DeviceConfig.getCustomPrintSpeed() * 20;
            }
            else
            {
                remainingPrintTime = currentPrint.speed() * 20;
            }
        }
        else
        {
            remainingPrintTime = state.animationTime;
        }
        totalPrintTime = remainingPrintTime;

        pipeline.setInteger("state", state.ordinal());
        pipeline.setInteger("totalPrintTime", totalPrintTime);
        pipeline.setInteger("remainingPrintTime", remainingPrintTime);
        sync();
    }

    public void addToQueue(IPrint print)
    {
        printQueue.offer(print);
    }

    private void print(IPrint print)
    {
        world.playSound(null, pos, DeviceSounds.PRINTER_LOADING_PAPER, SoundCategory.BLOCKS, 0.5F, 1.0F);

        setState(LOADING_PAPER);
        currentPrint = print;
        paperCount--;

        pipeline.setInteger("paperCount", paperCount);
        pipeline.setTag("currentPrint", IPrint.writeToTag(currentPrint));
        sync();
    }

    public boolean isLoading()
    {
        return state == LOADING_PAPER;
    }

    public boolean isPrinting()
    {
        return state == PRINTING;
    }

    public int getTotalPrintTime()
    {
        return totalPrintTime;
    }

    public int getRemainingPrintTime()
    {
        return remainingPrintTime;
    }

    public boolean addPaper(ItemStack stack, boolean addAll)
    {
        if(!stack.isEmpty() && stack.getItem() == Items.PAPER && paperCount < DeviceConfig.getMaxPaperCount())
        {
            if(!addAll)
            {
                paperCount++;
                stack.shrink(1);
            }
            else
            {
                paperCount += stack.getCount();
                stack.setCount(Math.max(0, paperCount - 64));
                paperCount = Math.min(64, paperCount);
            }
            pipeline.setInteger("paperCount", paperCount);
            sync();
            world.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    public boolean hasPaper()
    {
        return paperCount > 0;
    }

    public int getPaperCount()
    {
        return paperCount;
    }

    public IPrint getPrint()
    {
        return currentPrint;
    }

    public enum State
    {
        LOADING_PAPER(30), PRINTING(0), IDLE(0);

        final int animationTime;

        State(int time)
        {
            this.animationTime = time;
        }

        public State next()
        {
            if(ordinal() + 1 >= values().length)
                return null;
            return values()[ordinal() + 1];
        }
    }
}
