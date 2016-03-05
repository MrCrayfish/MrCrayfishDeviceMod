package com.mrcryafish.device;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class DeviceTab extends CreativeTabs 
{
	public DeviceTab(String label) 
	{
		super(label);
	}

	@Override
	public Item getTabIconItem() 
	{
		return Items.redstone;
	}
}
