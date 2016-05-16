package com.mrcrayfish.device.util;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiHelper 
{
	public static void drawModalRectWithUV(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
		drawModalRectWithUV(x, y, 0, u, v, width, height, textureWidth, textureHeight);
    }
	
	public static void drawModalRectWithUV(double x, double y, double z, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
		float scale = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)x, (double)(y + height), z).tex((double)(u * scale), (double)(v + textureHeight) * scale).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), z).tex((double)(u + textureWidth) * scale, (double)(v + textureHeight) * scale).endVertex();
        worldrenderer.pos((double)(x + width), (double)y, z).tex((double)(u + textureWidth) * scale, (double)(v * scale)).endVertex();
        worldrenderer.pos((double)x, (double)y, z).tex((double)(u * scale), (double)(v * scale)).endVertex();
        tessellator.draw();
    }
	
	public static boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}
}
