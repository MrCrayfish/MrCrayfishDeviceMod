package com.mrcrayfish.device.init;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;

import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DeviceTileEntites 
{
	public static void register()
	{
		GameRegistry.registerTileEntity(TileEntityLaptop.class, "cdm:laptop");
		GameRegistry.registerTileEntity(TileEntityPrinter.class, "cdm:printer");
	}
}
