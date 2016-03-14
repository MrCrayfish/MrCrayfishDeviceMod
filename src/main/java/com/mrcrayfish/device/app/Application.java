package com.mrcrayfish.device.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
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
	public int startX, startY;
	
	public final Layout defaultLayout;
	public Layout currentLayout;
	
	private boolean needsUpdate = false;
	
	public Application(String appId, String displayName, int width, int height) 
	{
		this.APP_ID = appId;
		this.DISPLAY_NAME = displayName;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.defaultLayout = new Layout();
	}
	
	public void addComponent(Component c)
	{
		if(c != null)
		{
			defaultLayout.addComponent(c);
			c.init(defaultLayout);
		}
	}
	
	public void setCurrentLayout(Layout layout)
	{
		this.currentLayout = layout;
		this.updateComponents(startX, startY);
	}
	
	public void restoreDefaultLayout()
	{
		this.setCurrentLayout(defaultLayout);
	}
	
	public void init(int x, int y)
	{
		this.setCurrentLayout(defaultLayout);
		this.startX = x;
		this.startY = y;
	}
	
	public void onTick() 
	{
		for(Component c : currentLayout.components)
		{
			c.handleTick();
		}
	}
	
	public void render(Gui gui, Minecraft mc, int x, int y, int mouseX, int mouseY)
	{
		for(Component c : currentLayout.components)
		{
			c.render(mc, mouseX, mouseY);
		}
	}
	
	public void handleClick(int mouseX, int mouseY, int mouseButton) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleClick(this, mouseX, mouseY, mouseButton);
		}
	}
	
	public void handleKeyTyped(char character, int code) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleKeyTyped(character, code);
		}
	}
	
	public void handleButtonClick(Button button) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleButtonClick(button);
		}
	}
	
	public void updateComponents(int x, int y)
	{
		for(Component c : currentLayout.components)
		{
			c.updateComponents(x, y);
		}
		
		this.startX = x;
		this.startY = y;
	}
	
	public void onClose()
	{
		defaultLayout.components.clear();
		currentLayout.components.clear();
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
