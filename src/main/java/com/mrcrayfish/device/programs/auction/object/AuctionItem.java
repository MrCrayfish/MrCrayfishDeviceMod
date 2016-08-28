package com.mrcrayfish.device.programs.auction.object;

import net.minecraft.item.ItemStack;

public class AuctionItem
{
	private ItemStack stack;
	private int price;
	
	public AuctionItem(ItemStack stack, int price)
	{
		this.stack = stack;
		this.price = price;
	}

	public ItemStack getStack()
	{
		return stack;
	}

	public int getPrice()
	{
		return price;
	}
}
