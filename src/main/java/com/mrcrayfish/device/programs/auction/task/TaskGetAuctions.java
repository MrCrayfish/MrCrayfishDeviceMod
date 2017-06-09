package com.mrcrayfish.device.programs.auction.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.auction.AuctionManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskGetAuctions extends Task
{
	public TaskGetAuctions()
	{
		super("minebay_get_auctions");
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) {}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) {}

	@Override
	public void prepareResponse(NBTTagCompound nbt)
	{
		AuctionManager.INSTANCE.writeToNBT(nbt);
		this.setSuccessful();
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {
		AuctionManager.INSTANCE.readFromNBT(nbt);
	}
}
