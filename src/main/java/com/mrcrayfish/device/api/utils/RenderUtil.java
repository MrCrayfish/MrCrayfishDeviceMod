package com.mrcrayfish.device.api.utils;

import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class RenderUtil
{
	public static void renderItem(int x, int y, ItemStack stack, boolean overlay)
	{
		GlStateManager.disableDepth();
		GlStateManager.enableLighting();
		RenderHelper.enableGUIStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		if(overlay) Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, stack, x, y);
		GlStateManager.enableAlpha();
		GlStateManager.disableLighting();
	}
	
	public static void drawRectWithTexture(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
		drawRectWithTexture(x, y, 0, u, v, width, height, textureWidth, textureHeight);
    }
	
	/**
	 * Texture size must be 256x256
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param textureWidth
	 * @param textureHeight
	 */
	public static void drawRectWithTexture(double x, double y, double z, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
		float scale = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, z).tex((double)(u * scale), (double)(v + textureHeight) * scale).endVertex();
        buffer.pos(x + width, y + height, z).tex((double)(u + textureWidth) * scale, (double)(v + textureHeight) * scale).endVertex();
        buffer.pos(x + width, y, z).tex((double)(u + textureWidth) * scale, (double)(v * scale)).endVertex();
        buffer.pos(x, y, z).tex((double)(u * scale), (double)(v * scale)).endVertex();
        tessellator.draw();
    }

	public static void drawRectWithFullTexture(double x, double y, float u, float v, int width, int height)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, 0).tex(0, 1).endVertex();
		buffer.pos(x + width, y + height, 0).tex(1, 1).endVertex();
		buffer.pos(x + width, y, 0).tex(1, 0).endVertex();
		buffer.pos(x, y, 0).tex(0, 0).endVertex();
		tessellator.draw();
	}

	public static void drawRectWithTexture(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight)
	{
		float scaleWidth = 1.0F / sourceWidth;
		float scaleHeight = 1.0F / sourceHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, 0).tex((double)(u * scaleWidth), (double)(v + textureHeight) * scaleHeight).endVertex();
		buffer.pos(x + width, y + height, 0).tex((double)(u + textureWidth) * scaleWidth, (double)(v + textureHeight) * scaleHeight).endVertex();
		buffer.pos(x + width, y, 0).tex((double)(u + textureWidth) * scaleWidth, (double)(v * scaleHeight)).endVertex();
		buffer.pos(x, y, 0).tex((double)(u * scaleWidth), (double)(v * scaleHeight)).endVertex();
		tessellator.draw();
	}

	public static void drawApplicationIcon(@Nullable AppInfo info, double x, double y)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(Laptop.ICON_TEXTURES);
		if(info != null)
		{
			drawRectWithTexture(x, y, info.getIconU(), info.getIconV(), 14, 14, 14, 14, 224, 224);
		}
		else
		{
			drawRectWithTexture(x, y, 0, 0, 14, 14, 14, 14, 224, 224);
		}
	}

	public static void drawStringClipped(String text, int x, int y, int width, int color, boolean shadow)
	{
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		String clipped = text;
		if(fontRenderer.getStringWidth(clipped) > width)
		{
			clipped = fontRenderer.trimStringToWidth(clipped, width - 8) + "...";
		}
		fontRenderer.drawString(clipped, x, y, color, shadow);
	}

	public static boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}
}
