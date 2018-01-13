package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.api.print.IPrint;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class TileEntityPaper extends TileEntitySync
{
    private IPrint print;
    private byte rotation;

    public void nextRotation()
    {
        rotation++;
        if(rotation > 7)
        {
            rotation = 0;
        }
        pipeline.setByte("rotation", rotation);
        sync();
        playSound(SoundEvents.ENTITY_ITEMFRAME_ROTATE_ITEM);
    }

    public float getRotation()
    {
        return rotation * 45F;
    }

    @Nullable
    public IPrint getPrint()
    {
        return print;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("print", Constants.NBT.TAG_COMPOUND))
        {
            print = IPrint.loadFromTag(compound.getCompoundTag("print"));
        }
        if(compound.hasKey("rotation", Constants.NBT.TAG_BYTE))
        {
            rotation = compound.getByte("rotation");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if(print != null)
        {
            compound.setTag("print", IPrint.writeToTag(print));
        }
        compound.setByte("rotation", rotation);
        return compound;
    }

    private void playSound(SoundEvent sound)
    {
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
