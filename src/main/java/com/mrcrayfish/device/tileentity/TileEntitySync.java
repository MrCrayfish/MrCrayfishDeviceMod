package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Author: MrCrayfish
 */
public class TileEntitySync extends TileEntity
{
    protected NBTTagCompound pipeline = new NBTTagCompound();

    protected void sync()
    {
        TileEntityUtil.markBlockForUpdate(world, pos);
        pipeline = new NBTTagCompound();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        if(!pipeline.hasNoTags())
        {
            NBTTagCompound updateTag = super.writeToNBT(pipeline);
            pipeline = new NBTTagCompound();
            return updateTag;
        }
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }
}
