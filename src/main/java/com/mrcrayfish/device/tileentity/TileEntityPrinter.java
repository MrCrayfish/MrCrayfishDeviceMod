package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.init.DeviceSounds;
import com.mrcrayfish.device.util.CollisionHelper;
import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.Constants;

import static com.mrcrayfish.device.tileentity.TileEntityPrinter.State.*;

/**
 * Author: MrCrayfish
 */
public class TileEntityPrinter extends TileEntity implements ITickable
{
    private String name = "Printer";
    private ItemStack item;
    private State state = IDLE;
    private int remainingPrintTime;

    private NBTTagCompound bufferTag = new NBTTagCompound();

    @Override
    public void update()
    {
        if(!world.isRemote)
        {
            if(remainingPrintTime > 0)
            {
                remainingPrintTime--;
                if(remainingPrintTime % 20 == 0 || state == LOADING_PAPER)
                {
                    bufferTag.setInteger("remainingPrintTime", remainingPrintTime);
                    TileEntityUtil.markBlockForUpdate(world, pos);
                    if(remainingPrintTime != 0 && state == PRINTING)
                    {
                        world.playSound(null, pos, DeviceSounds.printing_ink, SoundCategory.BLOCKS, 0.5F, 1.0F);
                    }
                }
            }
            else
            {
                setState(state.next());
            }
        }

        if(state == IDLE && item != null)
        {
            if(!world.isRemote)
            {
                IBlockState state = world.getBlockState(pos);
                double[] fixedPosition = CollisionHelper.fixRotation(state.getValue(BlockPrinter.FACING), 0.15, 0.5, 0.0, 0.0);
                EntityItem entity = new EntityItem(world, pos.getX() + fixedPosition[0], pos.getY() + 0.0625, pos.getZ() + fixedPosition[1], item);
                entity.motionX = 0;
                entity.motionY = 0;
                entity.motionZ = 0;
                world.spawnEntity(entity);
            }
            item = null;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("name", Constants.NBT.TAG_STRING))
        {
            this.name = compound.getString("name");
        }
        if(compound.hasKey("item", Constants.NBT.TAG_COMPOUND))
        {
            this.item = new ItemStack(compound.getCompoundTag("item"));
        }
        if(compound.hasKey("remainingPrintTime", Constants.NBT.TAG_INT))
        {
            this.remainingPrintTime = compound.getInteger("remainingPrintTime");
        }
        if(compound.hasKey("state", Constants.NBT.TAG_INT))
        {
            setState(State.values()[compound.getInteger("state")]);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setString("name", this.name);
        return super.writeToNBT(compound);
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


    public void setState(State state)
    {
        if(state == null)
            return;
        this.state = state;
        this.remainingPrintTime = state.animationTime;
        bufferTag.setInteger("state", state.ordinal());
        TileEntityUtil.markBlockForUpdate(world, pos);
    }

    public void print(ItemStack stack)
    {
        if(!stack.hasTagCompound())
            return;

        if(!stack.getTagCompound().hasKey("type", Constants.NBT.TAG_STRING))
            return;

        if(!stack.getTagCompound().hasKey("data", Constants.NBT.TAG_COMPOUND))
            return;

        setState(LOADING_PAPER);
        item = stack.copy();
        bufferTag.setInteger("state", state.ordinal());
        bufferTag.setTag("item", item.writeToNBT(new NBTTagCompound()));
        TileEntityUtil.markBlockForUpdate(world, pos);
        world.playSound(null, pos, DeviceSounds.printing_paper, SoundCategory.BLOCKS, 0.5F, 1.0F);
    }

    public boolean isLoading()
    {
        return state == LOADING_PAPER;
    }

    public boolean isPrinting()
    {
        return state == PRINTING || (state == IDLE && remainingPrintTime > 0);
    }

    public int getRemainingPrintTime()
    {
        return remainingPrintTime;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public enum State
    {
        LOADING_PAPER(20), PRINTING(400), IDLE(0);

        final int animationTime;

        State(int time)
        {
            this.animationTime = time;
        }

        public int getAnimationTime()
        {
            return animationTime;
        }

        public State next()
        {
            if(ordinal() + 1 >= values().length)
                return null;
            return values()[ordinal() + 1];
        }
    }
}
