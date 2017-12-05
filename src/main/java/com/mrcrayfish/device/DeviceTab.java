package com.mrcrayfish.device;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class DeviceTab extends CreativeTabs 
{
	public DeviceTab(String label) 
	{
		super(label);
	}

	@Override
	public ItemStack getTabIconItem() 
	{
		return new ItemStack(Items.REDSTONE);
	}
}
