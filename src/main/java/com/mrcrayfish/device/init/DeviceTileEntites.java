package com.mrcrayfish.device.init;

import com.mrcrayfish.device.tileentity.TileEntityLaptop;

import com.mrcrayfish.device.tileentity.TileEntityRouter;
import com.mrcrayfish.device.tileentity.TileEntityPaper;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DeviceTileEntites 
{
	public static void register()
    {
		GameRegistry.registerTileEntity(TileEntityLaptop.class, "cdm:laptop");
        GameRegistry.registerTileEntity(TileEntityRouter.class, "cdm:router");
		GameRegistry.registerTileEntity(TileEntityPrinter.class, "cdm:printer");
		GameRegistry.registerTileEntity(TileEntityPaper.class, "cdm:printed_paper");
	}
}
