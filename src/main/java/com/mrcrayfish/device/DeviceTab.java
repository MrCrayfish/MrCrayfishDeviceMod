package com.mrcrayfish.device;

import com.mrcrayfish.device.init.DeviceItems;

import net.minecraft.creativetab.CreativeTabs;
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
		return DeviceItems.laptop;
	}
	
}
