package com.mrcrayfish.device.programs.auction.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.auction.AuctionManager;
import com.mrcrayfish.device.programs.auction.object.AuctionItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskAddAuction extends Task
{
	private int slot;
	private int amount;
	private int price;
	private int duration;
	
	private AuctionItem item;
	
	public TaskAddAuction()
	{
		super("minebay_add_auction");
	}
	
	public TaskAddAuction(int slot, int amount, int price, int duration)
	{
		this();
		this.slot = slot;
		this.amount = amount;
		this.price = price;
		this.duration = duration;
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) 
	{
		nbt.setInteger("slot", slot);
		nbt.setInteger("amount", amount);
		nbt.setInteger("price", price);
		nbt.setInteger("duration", duration);
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		int slot = nbt.getInteger("slot");
		int amount = nbt.getInteger("amount");
		int price = nbt.getInteger("price");
		int duration = nbt.getInteger("duration");
		
		if(slot >= 0 && price >= 0 && slot < player.inventory.getSizeInventory())
		{
			ItemStack real = player.inventory.getStackInSlot(slot);
			if(real != null)
			{
				ItemStack stack = real.copy();
				stack.setCount(amount);
				real.shrink(amount);
				//TODO Test this
				
				item = new AuctionItem(stack, price, duration, player.getUniqueID());
				
				AuctionManager.INSTANCE.addItem(item);
				
				this.setSuccessful();
			}
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt)
	{
		if(isSucessful())
		{
			item.writeToNBT(nbt);
		}
	}

	@Override
	public void processResponse(NBTTagCompound nbt) 
	{
		if(isSucessful())
		{
			AuctionManager.INSTANCE.addItem(AuctionItem.readFromNBT(nbt));
		}
	}
}
