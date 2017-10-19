package com.mrcrayfish.device.init;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DeviceCrafting 
{
	public static void register()
	{
		ItemStack whiteLaptop = new ItemStack(DeviceItems.laptop, 1, 0);
		for(EnumDyeColor col : EnumDyeColor.values()) {
			if(col != EnumDyeColor.WHITE) {
				GameRegistry.addShapelessRecipe(new ItemStack(DeviceItems.laptop, 1, col.getMetadata()), whiteLaptop, new ItemStack(Items.DYE, 1, col.getDyeDamage()));
			}
		}
	}
}
