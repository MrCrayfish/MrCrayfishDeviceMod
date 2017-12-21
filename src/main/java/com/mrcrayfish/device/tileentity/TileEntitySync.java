package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Author: MrCrayfish
 */
public abstract class TileEntitySync extends TileEntity
{
    protected NBTTagCompound pipeline = new NBTTagCompound();

    public void sync()
    {
        TileEntityUtil.markBlockForUpdate(world, pos);
        markDirty();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public final NBTTagCompound getUpdateTag()
    {
        if(!pipeline.hasNoTags())
        {
            NBTTagCompound updateTag = super.writeToNBT(pipeline);
            pipeline = new NBTTagCompound();
            return updateTag;
        }
        return super.writeToNBT(writeSyncTag());
    }

    public abstract NBTTagCompound writeSyncTag();

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }
}
