package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.init.DeviceSounds;
import com.mrcrayfish.device.util.CollisionHelper;
import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

import static com.mrcrayfish.device.tileentity.TileEntityPrinter.State.*;

/**
 * Author: MrCrayfish
 */
public class TileEntityPrinter extends TileEntity implements ITickable
{
    private static final int MAX_PAPER = 64;

    private String name = "Printer";
    private IPrint currentPrint;
    private State state = IDLE;
    private int totalPrintTime;
    private int remainingPrintTime;
    private int paperCount = 0;

    private NBTTagCompound bufferTag = new NBTTagCompound();

    @Override
    public void update()
    {
        if(!world.isRemote)
        {
            if(remainingPrintTime > 0)
            {
                if(remainingPrintTime % 20 == 0 || state == LOADING_PAPER)
                {
                    bufferTag.setInteger("remainingPrintTime", remainingPrintTime);
                    TileEntityUtil.markBlockForUpdate(world, pos);
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
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("name", Constants.NBT.TAG_STRING))
        {
            name = compound.getString("name");
        }
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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setString("name", name);
        compound.setInteger("totalPrintTime", totalPrintTime);
        compound.setInteger("remainingPrintTime", remainingPrintTime);
        compound.setInteger("state", state.ordinal());
        compound.setInteger("paperCount", paperCount);
        if(currentPrint != null)
        {
            compound.setTag("currentPrint", IPrint.writeToTag(currentPrint));
        }
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        NBTTagCompound tag = pkt.getNbtCompound();
        this.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        if(!bufferTag.hasNoTags())
        {
            NBTTagCompound updateTag = super.writeToNBT(bufferTag);
            bufferTag = new NBTTagCompound();
            return updateTag;
        }
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 3, getUpdateTag());
    }

    public void setState(State newState)
    {
        if(newState == null)
            return;

        state = newState;
        remainingPrintTime = state == PRINTING ? currentPrint.speed() * 20 : state.animationTime;
        totalPrintTime = remainingPrintTime;

        bufferTag.setInteger("state", state.ordinal());
        bufferTag.setInteger("totalPrintTime", totalPrintTime);
        bufferTag.setInteger("remainingPrintTime", remainingPrintTime);

        TileEntityUtil.markBlockForUpdate(world, pos);
        markDirty();
    }

    public void print(IPrint print)
    {
        if(paperCount <= 0)
            return;

        world.playSound(null, pos, DeviceSounds.PRINTER_LOADING_PAPER, SoundCategory.BLOCKS, 0.5F, 1.0F);

        setState(LOADING_PAPER);
        currentPrint = print;
        paperCount--;

        bufferTag.setInteger("paperCount", paperCount);
        bufferTag.setTag("currentPrint", IPrint.writeToTag(currentPrint));

        TileEntityUtil.markBlockForUpdate(world, pos);
        markDirty();
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
        if(!stack.isEmpty() && stack.getItem() == Items.PAPER && paperCount < MAX_PAPER)
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
            bufferTag.setInteger("paperCount", paperCount);
            world.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            TileEntityUtil.markBlockForUpdate(world, pos);
            markDirty();
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

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public IPrint getPrint()
    {
        return currentPrint;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentString(name);
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
