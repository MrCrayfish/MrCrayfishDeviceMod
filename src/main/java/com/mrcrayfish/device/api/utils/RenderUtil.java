package com.mrcrayfish.device.api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

public class RenderUtil
{
	public static void renderItem(int x, int y, ItemStack stack, boolean overlay)
	{
		GlStateManager.disableDepth();
		GlStateManager.enableLighting();
		RenderHelper.enableGUIStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		if(overlay) Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, stack, x, y);
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
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos((double)x, (double)(y + height), z).tex((double)(u * scale), (double)(v + textureHeight) * scale).endVertex();
        buffer.pos((double)(x + width), (double)(y + height), z).tex((double)(u + textureWidth) * scale, (double)(v + textureHeight) * scale).endVertex();
        buffer.pos((double)(x + width), (double)y, z).tex((double)(u + textureWidth) * scale, (double)(v * scale)).endVertex();
        buffer.pos((double)x, (double)y, z).tex((double)(u * scale), (double)(v * scale)).endVertex();
        tessellator.draw();
    }
	
	public static boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}
}
