package com.mrcrayfish.device.proxy;

import com.mrcrayfish.device.api.app.Application;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class CommonProxy
{
	public void preInit() {}

	public void init() {}

	public void postInit() {}

	@Nullable
	public Application registerApplication(ResourceLocation identifier, Class<? extends Application> clazz)
	{
		return null;
	}
}
