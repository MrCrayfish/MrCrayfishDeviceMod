package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class TileEntityPrinter extends TileEntity implements ITickable
{
    public static final int PRINT_TIME = 400;

    private String name = "Printer";
    private ItemStack item;
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
                if(remainingPrintTime % 20 == 0)
                {
                    bufferTag.setInteger("remainingPrintTime", remainingPrintTime);
                    TileEntityUtil.markBlockForUpdate(world, pos);
                }
            }
        }

        if(remainingPrintTime == 0 && item != null)
        {
            if(!world.isRemote)
            {
                EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, item);
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

    public void print(ItemStack stack)
    {
        if(!stack.hasTagCompound())
            return;

        if(!stack.getTagCompound().hasKey("type", Constants.NBT.TAG_STRING))
            return;

        if(!stack.getTagCompound().hasKey("data", Constants.NBT.TAG_COMPOUND))
            return;

        remainingPrintTime = PRINT_TIME;
        item = stack.copy();

        bufferTag.setInteger("remainingPrintTime", remainingPrintTime);
        bufferTag.setTag("item", item.writeToNBT(new NBTTagCompound()));
        TileEntityUtil.markBlockForUpdate(world, pos);
    }

    public boolean isPrinting()
    {
        return item != null && remainingPrintTime > 0;
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
}
