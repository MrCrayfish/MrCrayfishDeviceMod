package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.util.TileEntityUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * <b>Author: MrCrayfish</b>
 */
public abstract class TileEntitySync extends TileEntity
{
	/** A default tag that is used to sync with the client and server */
	private NBTTagCompound pipeline = new NBTTagCompound();

	/**
	 * Syncs this tile entity with the client and server.
	 */
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
		if (!pipeline.hasNoTags())
		{
			NBTTagCompound updateTag = super.writeToNBT(pipeline);
			pipeline = new NBTTagCompound();
			return updateTag;
		}
		return super.writeToNBT(writeSyncTag());
	}

	/**
	 * Writes this tile entity's objects as to update with the client. Will not be called is {@link #pipeline} is used.
	 * 
	 * @return The tag that will be written
	 */
	public abstract NBTTagCompound writeSyncTag();

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}

	/**
	 * @return The tag that is used for updating between the client and server.
	 */
	public NBTTagCompound getPipeline()
	{
		return pipeline;
	}
}
