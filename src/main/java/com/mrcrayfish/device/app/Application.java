package com.mrcrayfish.device.app;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.gui.GuiLaptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public abstract class Application 
{
	private final String APP_ID;
	private final String DISPLAY_NAME;
	private final int WIDTH, HEIGHT;
	
	private boolean needsUpdate = false;
	
	private List<Component> components;
	
	public Application(String appId, String displayName, int width, int height) 
	{
		this.APP_ID = appId;
		this.DISPLAY_NAME = displayName;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.components = new ArrayList<Component>();
	}
	
	public void addComponent(Component c)
	{
		if(this.components != null)
		{
			this.components.add(c);
		}
	}
	
	public void init(int x, int y)
	{
		this.components.clear();
	}
	
	public void onTick() {};
	
	public void render(Gui gui, Minecraft mc, int x, int y, int mouseX, int mouseY)
	{
		for(Component c : components)
		{
			c.render(mc, mouseX, mouseY);
		}
	}
	
	public void handleClick(int mouseX, int mouseY, int mouseButton) 
	{
		for(Component c : components)
		{
			c.handleClick(this, mouseX, mouseY, mouseButton);
		}
	}
	
	public void handleKeyTyped(char character, int code) 
	{
		for(Component c : components)
		{
			c.handleKeyTyped(character, code);
		}
	}
	
	public void handleButtonClick(Button button) 
	{
		for(Component c : components)
		{
			c.handleButtonClick(button);
		}
	}
	
	public void updateComponents(int x, int y)
	{
		for(Component c : components)
		{
			
			c.xPosition = x + c.left;
			c.yPosition = y + c.top;
		}
	}
	
	public void onClose()
	{
		components.clear();
	}

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
