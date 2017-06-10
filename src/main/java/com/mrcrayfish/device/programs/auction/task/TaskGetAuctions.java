package com.mrcrayfish.device.programs.auction.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.auction.AuctionManager;

import com.mrcrayfish.device.programs.auction.object.AuctionItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class TaskGetAuctions extends Task
{
	private UUID seller;

	public TaskGetAuctions()
	{
		super("minebay_get_auctions");
	}

	public TaskGetAuctions(UUID seller)
	{
		this();
		this.seller = seller;
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt)
	{
		if(seller != null)
		{
			nbt.setString("seller", seller.toString());
		}
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
	{
		if(nbt.hasKey("seller"))
		{
			seller = UUID.fromString(nbt.getString("seller"));
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt)
	{
		if(seller != null)
		{
			List<AuctionItem> items = AuctionManager.INSTANCE.getItemsForSeller(seller);
			NBTTagList tagList = new NBTTagList();
			items.forEach(i -> {
				NBTTagCompound itemTag = new NBTTagCompound();
				i.writeToNBT(itemTag);
				tagList.appendTag(itemTag);
			});
			nbt.setTag("auctionItems", tagList);
		}
		else
		{
			AuctionManager.INSTANCE.writeToNBT(nbt);
		}
		this.setSuccessful();
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {
		AuctionManager.INSTANCE.readFromNBT(nbt);
	}
}
