package com.mrcrayfish.device.app.components;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.gui.GuiLaptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public abstract class Application 
{
	private final String APP_ID;
	private final String DISPLAY_NAME;
	private final ResourceLocation APP_GUI;
	private final int WIDTH, HEIGHT;
	
	private boolean needsUpdate = false;
	
	public Application(String appId, String displayName, int width, int height) 
	{
		this.APP_ID = appId;
		this.DISPLAY_NAME = displayName;
		this.APP_GUI = new ResourceLocation("cfm:textures/gui/" + appId + ".png");
		this.WIDTH = width;
		this.HEIGHT = height;
	}
	
	public abstract void init(List<GuiButton> buttons, int x, int y);
	
	public void onTick() {};
	
	public abstract void render(Gui gui, Minecraft mc, int x, int y);
	
	public void handleClick(Gui gui, int mouseX, int mouseY, int mouseButton) {};
	
	public void handleKeyTyped(char character, int code) {};
	
	public abstract void handleButtonClick(GuiButton button);
	
	public abstract void updateButtons(int x, int y);
	
	public abstract void hideButtons(List<GuiButton> buttons);
	
	public abstract void load(NBTTagCompound tagCompound);
	
	public abstract void save(NBTTagCompound tagCompound);
	
	public void markDirty() 
	{
		needsUpdate = true;
	}
	
	public boolean isDirty() 
	{
		return needsUpdate;
	}
	
	public void clean() 
	{
		needsUpdate = false;
	}
	
	public String getID()
	{
		return APP_ID;
	}
	
	public String getDisplayName()
	{
		return DISPLAY_NAME;
	}
	
	public int getWidth() 
	{
		return WIDTH;
	}
	
	public int getHeight() 
	{
		return HEIGHT;
	}

}
