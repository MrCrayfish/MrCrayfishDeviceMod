package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.api.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public interface IIcon
{
	ResourceLocation getIconAsset();
	
	default int getIconWidth() {
		return 10;
	}
	default int getIconHeight() {
		return 10;
	}

	int getTextureWidth();
	int getTextureHeight();

	int getGridWidth();
	int getGridHeight();

	default int getU()
	{
		return (ordinal() % getGridWidth()) * getTextureWidth();
	}

	default int getV()
	{
		return (ordinal() / getGridHeight()) * getTextureHeight();
	}

	int ordinal();

    default void draw(Minecraft mc, int x, int y)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(getIconAsset());
		int assetWidth = getGridWidth() * getIconWidth();
		int assetHeight = getGridHeight() * getIconHeight();
		RenderUtil.drawRectWithTexture(x, y, getU(), getV(), getIconWidth(), getIconHeight(), getTextureWidth(), getTextureHeight(), assetWidth, assetHeight);
	}
}
