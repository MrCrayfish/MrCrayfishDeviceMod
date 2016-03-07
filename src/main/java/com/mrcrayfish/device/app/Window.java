package com.mrcrayfish.device.app;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Window 
{
	private static final ResourceLocation WINDOW_GUI = new ResourceLocation("cdm:textures/gui/window.png");
	
	private int x, y;
	private int width, height;
	
	public Window(int x, int y, int width, int height) 
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void init(List<GuiButton> buttonList)
	{
		
	}
	
	public void render(Gui gui, Minecraft mc)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(WINDOW_GUI);
		
		/* Corners */
		gui.drawTexturedModalRect(x, y, 0, 0, 5, 5);
		gui.drawTexturedModalRect(x + width - 5, y, 6, 0, 5, 5);
		gui.drawTexturedModalRect(x + width - 5, y + height - 5, 6, 6, 8, 8);
		gui.drawTexturedModalRect(x, y + height - 5, 0, 6, 5, 5);
		
		/* Edges */
		drawModalRectWithUV(x + 5, y, 5, 0, width - 10, 5, 1, 5);
		drawModalRectWithUV(x + width - 5, y + 5, 6, 5, 5, height - 10, 5, 1);
		drawModalRectWithUV(x + 5, y + height - 5, 5, 6, width - 10, 5, 1, 5);
		drawModalRectWithUV(x, y + 5, 0, 5, 5, height - 10, 5, 1);
		
		/* Center */
		drawModalRectWithUV(x + 5, y + 5, 5, 5, width - 10, height - 10, 1, 1);
	}
	
	public static void drawModalRectWithUV(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
		float scale = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)x, (double)(y + height), 0.0D).tex((double)(u * scale), (double)(v + textureHeight) * scale).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)(u + textureWidth) * scale, (double)(v + textureHeight) * scale).endVertex();
        worldrenderer.pos((double)(x + width), (double)y, 0.0D).tex((double)(u + textureWidth) * scale, (double)(v * scale)).endVertex();
        worldrenderer.pos((double)x, (double)y, 0.0D).tex((double)(u * scale), (double)(v * scale)).endVertex();
        tessellator.draw();
    }
}
