package com.mrcrayfish.device;

import com.mrcrayfish.device.init.DeviceBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
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
		return new ItemStack(DeviceBlocks.LAPTOP, 1, EnumDyeColor.RED.getMetadata());
	}
}
