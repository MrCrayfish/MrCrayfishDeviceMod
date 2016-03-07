package com.mrcrayfish.device.app;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Window 
{
	private static final ResourceLocation WINDOW_GUI = new ResourceLocation("cdm:textures/gui/application.png");
	
	private int x, y;
	private int width, height;
	
	private int offsetX, offsetY;
	private int lastMouseX;
	
	public Window(int x, int y, int width, int height) 
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		if(width > 236)
		{
			this.width = 236;
		}
		if(height > 120)
		{
			this.height = 120;
		}
	}
	
	public void init(List<GuiButton> buttonList)
	{
		
	}
	
	public void render(Gui gui, Minecraft mc, int x, int y)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(WINDOW_GUI);
		
		/* Corners */
		/*gui.drawTexturedModalRect(x, y, 0, 0, 5, 5);
		gui.drawTexturedModalRect(x + width - 5, y, 6, 0, 5, 5);
		gui.drawTexturedModalRect(x + width - 5, y + height - 5, 6, 6, 8, 8);
		gui.drawTexturedModalRect(x, y + height - 5, 0, 6, 5, 5);*/
		
		/* Edges */
		/*drawModalRectWithUV(x + 5, y, 5, 0, width - 10, 5, 1, 5);
		drawModalRectWithUV(x + width - 5, y + 5, 6, 5, 5, height - 10, 5, 1);
		drawModalRectWithUV(x + 5, y + height - 5, 5, 6, width - 10, 5, 1, 5);
		drawModalRectWithUV(x, y + 5, 0, 5, 5, height - 10, 5, 1);*/
		
		/* Center */
		/*drawModalRectWithUV(x + 5, y + 5, 5, 5, width - 10, height - 10, 1, 1);*/
		
		x += offsetX;
		y += offsetY;
		
		//System.out.println(offsetX);
		
		/* Corners */
		gui.drawTexturedModalRect(x, y, 0, 0, 1, 13);
		gui.drawTexturedModalRect(x + width - 13, y, 2, 0, 13, 13);
		gui.drawTexturedModalRect(x + width - 1, y + height - 1, 14, 14, 1, 1);
		gui.drawTexturedModalRect(x, y + height - 1, 0, 14, 1, 1);
		
		/* Edges */
		drawModalRectWithUV(x + 1, y, 1, 0, width - 14, 13, 1, 13);
		drawModalRectWithUV(x + width - 1, y + 13, 14, 13, 1, height - 14, 1, 1);
		drawModalRectWithUV(x + 1, y + height - 1, 1, 14, width - 2, 1, 13, 1);
		drawModalRectWithUV(x, y + 13, 0, 13, 1, height - 14, 1, 1);
		
		/* Center */
		drawModalRectWithUV(x + 1, y + 13, 1, 13, width - 2, height - 14, 13, 1);
		
		mc.fontRendererObj.drawString("Note Stash", x + 3, y + 3, Color.WHITE.getRGB(), true);
		
		GlStateManager.disableBlend();
	}
	
	public void handleDrag(int mouseX, int mouseY)
	{
		
		this.offsetX += mouseX;
		this.offsetY += mouseY;
		System.out.println(mouseX + " " + mouseY);
	}
	
	public int getWidth() 
	{
		return width;
	}
	
	public int getHeight() 
	{
		return height;
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
