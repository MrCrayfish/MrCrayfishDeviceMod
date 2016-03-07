package com.mrcrayfish.device.app;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.gui.GuiLaptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public abstract class Application 
{
	private static final int APP_WIDTH = 100;
	private static final int APP_HEIGHT = 100;
	
	private final String displayName;
	private final ResourceLocation APP_GUI;
	
	public Application(String displayName, String resourseName) 
	{
		this.displayName = displayName;
		this.APP_GUI = new ResourceLocation("cfm:textures/gui/" + resourseName + ".png");
	}
	
	public abstract void init();
	
	public void render(GuiLaptop gui, Minecraft mc, int x, int y, int width, int height, float partialTicks)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(APP_GUI);
		int posX = width / 2;
		int posY = height / 2;
		gui.drawTexturedModalRect(posX, posY, 0, 0, APP_WIDTH, APP_HEIGHT);
	}
	
	public abstract void handleClick(GuiButton button);
	
	public abstract void hideAllButtons();
	
	public String getDisplayName()
	{
		return displayName;
	}
	
}
