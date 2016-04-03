package com.mrcrayfish.device.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mrcrayfish.device.app.components.Button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public abstract class Application 
{
	private final String APP_ID;
	private final String DISPLAY_NAME;
	private int width, height;
	private int startX, startY;
	private final Layout defaultLayout;
	private Layout currentLayout;
	
	/* If set to true, will update NBT data for Application */
	private boolean needsDataUpdate = false;
	
	/* If set to true, will update layout */
	boolean pendingLayoutUpdate = false;
	
	public Application(String appId, String displayName) 
	{
		this.APP_ID = appId;
		this.DISPLAY_NAME = displayName;
		this.defaultLayout = new Layout();
	}
	
	protected void addComponent(Component c)
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
		this.width = layout.width;
		this.height = layout.height;
		this.pendingLayoutUpdate = true;
	}
	
	public Layout getCurrentLayout() 
	{
		return currentLayout;
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
	
	public void render(Gui gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active)
	{
		for(Component c : currentLayout.components)
		{
			c.render(mc, mouseX, mouseY, active);
		}
	}
	
	public void handleClick(int mouseX, int mouseY, int mouseButton) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleClick(this, mouseX, mouseY, mouseButton);
		}
	}
	
	public void handleDrag(int mouseX, int mouseY)
	{
		for(Component c : currentLayout.components)
		{
			c.handleDrag(mouseX, mouseY);
		}
	}
	
	public void handleRelease(int mouseX, int mouseY) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleRelease(mouseX, mouseY);
		}
	}
	
	public void handleKeyTyped(char character, int code) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleKeyTyped(character, code);
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
		needsDataUpdate = true;
	}
	
	public boolean isDirty() 
	{
		return needsDataUpdate;
	}
	
	public void clean() 
	{
		needsDataUpdate = false;
	}
	
	public String getID()
	{
		return APP_ID;
	}
	
	public String getDisplayName()
	{
		return DISPLAY_NAME;
	}
	
	public void setDefaultWidth(int width)
	{
		this.defaultLayout.width = width;
	}
	
	public void setDefaultHeight(int height)
	{
		this.defaultLayout.height = height;
	}
	
	public int getWidth() 
	{
		return width;
	}
	
	public int getHeight() 
	{
		return height;
	}
}
