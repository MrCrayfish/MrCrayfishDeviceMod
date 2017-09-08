package com.mrcrayfish.device.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil
{
	public static int getItemAmount(EntityPlayer player, Item item)
	{
		int amount = 0;
		for(int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(stack != null && stack.getItem() == item)
			{
				amount += stack.getCount();
			}
		}
		return amount;
	}
	
	public static boolean hasItemAndAmount(EntityPlayer player, Item item, int amount)
	{
		int count = 0;
		for(ItemStack stack : player.inventory.mainInventory)
		{
			if(stack != null && stack.getItem() == item)
			{
				count += stack.getCount();
			}
		}
		return amount <= count;
	}
	
	public static boolean removeItemWithAmount(EntityPlayer player, Item item, int amount)
	{
		if(hasItemAndAmount(player, item, amount))
		{
			for(int i = 0; i < player.inventory.getSizeInventory(); i++)
			{
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack != null && stack.getItem() == item)
				{
					if(amount - stack.getCount() < 0)
					{
						stack.shrink(amount);
						return true;
					}
					else
					{
						amount -= stack.getCount();
						player.inventory.mainInventory.set(i, ItemStack.EMPTY);
						if(amount == 0) return true;
					}
				}
			}
		}
		return false;
	}
}
