package com.mrcrayfish.device.programs.auction.task;

import java.util.UUID;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.programs.auction.AuctionManager;
import com.mrcrayfish.device.programs.auction.object.AuctionItem;
import com.mrcrayfish.device.programs.system.object.Account;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskBuyItem extends Task
{
	private UUID id;
	
	public TaskBuyItem()
	{
		super("minebay_buy_item");
	}
	
	public TaskBuyItem(UUID id)
	{
		this();
		this.id = id;
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt)
	{
		nbt.setString("id", id.toString());
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
	{
		this.id = UUID.fromString(nbt.getString("id"));
		AuctionItem item = AuctionManager.INSTANCE.getItem(id);
		if(item != null && item.isValid())
		{
			int price = item.getPrice();
			Account buyer = BankUtil.INSTANCE.getAccount(player);
			Account seller = BankUtil.INSTANCE.getAccount(item.getSellerId());
			if(buyer.pay(seller, price))
			{
				item.setSold();
				world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, item.getStack().copy()));
				this.setSuccessful();
			}
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) {}

	@Override
	public void processResponse(NBTTagCompound nbt) {}
}
