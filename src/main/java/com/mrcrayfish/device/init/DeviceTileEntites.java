package com.mrcrayfish.device.init;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class DeviceTileEntites 
{
	public static void register()
	{
		GameRegistry.registerTileEntity(TileEntityLaptop.class, Reference.MOD_ID + "Laptop");
	}
}
