package com.mrcrayfish.device.gui;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.app.Window;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiLaptop extends GuiScreen 
{
	public static final int ID = 0;
	
	private static final ResourceLocation LAPTOP_GUI = new ResourceLocation("cdm:textures/gui/laptop.png");
	
	private Window window;
	
	public GuiLaptop()
	{
		this.window = new Window(0, 0, 100, 100);
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
		
		window.render(this, mc);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
