package com.mrcrayfish.device.proxy;

import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import com.mrcrayfish.device.tileentity.render.LaptopRenderer;

import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy implements IProxyInterface 
{
	@Override
	public void preInit() {}

	@Override
	public void init() 
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaptop.class, new LaptopRenderer());
	}

	@Override
	public void postInit() {}

}
