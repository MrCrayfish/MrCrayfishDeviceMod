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
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public abstract class Application 
{
	protected ResourceLocation icon;
	protected int u, v;
	
	private final String APP_ID;
	private final String DISPLAY_NAME;
	
	private int width, height;
	private int startX, startY;
	
	private final Layout defaultLayout;
	private Layout currentLayout;
	
	/** If set to true, will update NBT data for Application */
	private boolean needsDataUpdate = false;
	
	/* If set to true, will update layout */
	boolean pendingLayoutUpdate = false;
	
	protected Application(String appId, String displayName) 
	{
		this(appId, displayName, null, 0, 0);
	}
	
	protected Application(String appId, String displayName, ResourceLocation icon, int iconU, int iconV) 
	{
		this.APP_ID = appId;
		this.DISPLAY_NAME = displayName;
		this.defaultLayout = new Layout();
		this.icon = icon;
		this.u = iconU;
		this.v = iconV;
	}

	protected void addComponent(Component c)
	{
		if(c != null)
		{
			defaultLayout.addComponent(c);
			c.init(defaultLayout);
		}
	}
	
	protected void setCurrentLayout(Layout layout)
	{
		this.currentLayout = layout;
		this.width = layout.width;
		this.height = layout.height;
		this.pendingLayoutUpdate = true;
		this.currentLayout.init();
	}
	
	protected Layout getCurrentLayout() 
	{
		return currentLayout;
	}
	
	protected void restoreDefaultLayout()
	{
		this.setCurrentLayout(defaultLayout);
	}
	
	protected void init(int x, int y)
	{
		this.setCurrentLayout(defaultLayout);
		this.startX = x;
		this.startY = y;
	}
	
	void onTick() 
	{
		for(Component c : currentLayout.components)
		{
			c.handleTick();
		}
	}
	
	void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active)
	{
		currentLayout.render(laptop, mc, x, y);
		
		for(Component c : currentLayout.components)
		{
			c.render(laptop, mc, mouseX, mouseY, active);
		}
		
		for(Component c : currentLayout.components)
		{
			c.renderOverlay(laptop, mc, mouseX, mouseY, active);
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
	}
	
	void handleClick(int mouseX, int mouseY, int mouseButton) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleClick(this, mouseX, mouseY, mouseButton);
		}
	}
	
	void handleDrag(int mouseX, int mouseY)
	{
		for(Component c : currentLayout.components)
		{
			c.handleDrag(mouseX, mouseY);
		}
	}
	
	void handleRelease(int mouseX, int mouseY) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleRelease(mouseX, mouseY);
		}
	}
	
	void handleKeyTyped(char character, int code) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleKeyTyped(character, code);
		}
	}

	void updateComponents(int x, int y)
	{
		for(Component c : currentLayout.components)
		{
			c.updateComponents(x, y);
		}
		
		this.startX = x;
		this.startY = y;
	}
	
	void onClose()
	{
		defaultLayout.components.clear();
		currentLayout = null;
	}

	protected abstract void load(NBTTagCompound tagCompound);
	
	protected abstract void save(NBTTagCompound tagCompound);
	
	protected void setDefaultWidth(int width)
	{
		this.defaultLayout.width = width;
	}
	
	protected void setDefaultHeight(int height)
	{
		this.defaultLayout.height = height;
	}
	
	protected void markDirty() 
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
	
	protected String getDisplayName()
	{
		return DISPLAY_NAME;
	}
	
	public int getWidth() 
	{
		return width;
	}
	
	public int getHeight() 
	{
		return height;
	}
	
	public String getTitle()
	{
		return DISPLAY_NAME;
	}
}
