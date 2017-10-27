package io.github.lukas2005.DeviceModApps;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public interface IIcon {

	public ResourceLocation getIconAsset();
	
	public int getIconSize();
	
	public int getGridSize();
	
	public int getU();

    public int getV();

    public void draw(Minecraft mc, float x, float y);
	
}
