package com.mrcrayfish.device.programs.auction.object;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AuctionItem
{
	private UUID id;
	private ItemStack stack;
	private int price;
	private long timeLeft;
	private UUID sellerId;
	
	public AuctionItem(ItemStack stack, int price, long timeLeft, UUID sellerId)
	{
		this.id = UUID.randomUUID();
		this.stack = stack;
		this.price = price;
		this.timeLeft = timeLeft;
		this.sellerId = sellerId;
	}
	
	public AuctionItem(UUID id, ItemStack stack, int price, long timeLeft, UUID sellerId)
	{
		this.id = id;
		this.stack = stack;
		this.price = price;
		this.timeLeft = timeLeft;
		this.sellerId = sellerId;
	}
	
	public UUID getId()
	{
		return id;
	}

	public ItemStack getStack()
	{
		return stack;
	}

	public int getPrice()
	{
		return price;
	}
	
	public UUID getSellerId()
	{
		return sellerId;
	}
	
	public boolean isValid()
	{
		return timeLeft > 0;
	}
	
	public void decrementTime() 
	{
		if(timeLeft > 0)
		{
			timeLeft--;
		}
	}
	
	public long getTimeLeft()
	{
		return timeLeft;
	}
	
	public void setSold()
	{
		this.timeLeft = 0;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("id", id.toString());
		NBTTagCompound item = new NBTTagCompound();
		stack.writeToNBT(item);
		tag.setTag("item", item);
		tag.setInteger("price", price);
		tag.setLong("time", timeLeft);
		tag.setString("seller", sellerId.toString());
	}
	
	public static AuctionItem readFromNBT(NBTTagCompound tag)
	{
		UUID id = UUID.fromString(tag.getString("id"));
		NBTTagCompound item = tag.getCompoundTag("item");
		ItemStack stack = new ItemStack(item);
		int price = tag.getInteger("price");
		long timeLeft = tag.getLong("time");
		UUID sellerId = UUID.fromString(tag.getString("seller"));
		return new AuctionItem(id, stack, price, timeLeft, sellerId);
	}
	
	@Override
	public String toString()
	{
		return "{ " + id + ", " + stack + ", " + price + ", " + timeLeft + ", " + sellerId + " }"; 
	}
}
