package com.mrcrayfish.device.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.app.ApplicationBar;
import com.mrcrayfish.device.app.Window;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiLaptop extends GuiScreen 
{
	public static final int ID = 0;
	
	private static final ResourceLocation LAPTOP_GUI = new ResourceLocation("cdm:textures/gui/laptop.png");
	private static final ResourceLocation LAPTOP_WALLPAPER_1 = new ResourceLocation("cdm:textures/gui/laptop_wallpaper_1.png");
	
	private ApplicationBar bar;
	private Window window;
	
	private int lastMouseX, lastMouseY;
	
	public GuiLaptop()
	{
		this.window = new Window(0, 0, 100, 100);
		this.bar = new ApplicationBar();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		this.drawDefaultBackground();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(LAPTOP_GUI);
		
		int posX = (width - 256) / 2;
		int posY = (height - 158) / 2;
		this.drawTexturedModalRect(posX, posY, 0, 0, 256, 158);
		
		mc.getTextureManager().bindTexture(LAPTOP_WALLPAPER_1);
		this.drawTexturedModalRect(posX + 10, posY + 10, 0, 0, 256, 158);
		
		bar.render(this, mc, posX + 10, posY + 130);
		window.render(this, mc, posX + (256 - window.getWidth()) / 2, posY + 10 + (120 - window.getHeight()) / 2);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException 
	{
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) 
	{
		window.handleDrag(-(lastMouseX - mouseX), -(lastMouseY - mouseY));
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
	}
}
