package com.mrcrayfish.device.api.app;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
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
	
	private final Layout defaultLayout;
	private Layout currentLayout;
	
	/** If set to true, will update NBT data for Application */
	private boolean needsDataUpdate = false;
	
	/* If set to true, will update layout */
	boolean pendingLayoutUpdate = false;
	
	public Application(String appId, String displayName) 
	{
		this(appId, displayName, null, 0, 0);
	}
	
	public Application(String appId, String displayName, ResourceLocation icon, int iconU, int iconV) 
	{
		this.APP_ID = appId;
		this.DISPLAY_NAME = displayName;
		this.icon = icon;
		this.u = iconU;
		this.v = iconV;
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
	
	//TODO for public api, make protected
	public void restoreDefaultLayout()
	{
		this.setCurrentLayout(defaultLayout);
	}
	
	//TODO for public api, make protected
	public void init(int x, int y)
	{
		this.defaultLayout.clear();
		this.setCurrentLayout(defaultLayout);
	}
	
	//TODO for public api, make default visibilty
	public void onTick() 
	{
		for(Component c : currentLayout.components)
		{
			c.handleTick();
		}
	}
	
	//TODO Remove laptop instance
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
	{
		currentLayout.render(laptop, mc, x, y);
		
		for(Component c : currentLayout.components)
		{
			c.render(laptop, mc, mouseX, mouseY, active, partialTicks);
		}
		
		for(Component c : currentLayout.components)
		{
			c.renderOverlay(laptop, mc, mouseX, mouseY, active);
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
	}
	
	public void handleClick(int mouseX, int mouseY, int mouseButton) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleClick(this, mouseX, mouseY, mouseButton);
		}
	}
	
	public void handleDrag(int mouseX, int mouseY, int mouseButton)
	{
		for(Component c : currentLayout.components)
		{
			c.handleDrag(mouseX, mouseY, mouseButton);
		}
	}
	
	public void handleRelease(int mouseX, int mouseY, int mouseButton) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleRelease(mouseX, mouseY, mouseButton);
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
	}
	
	public void onClose()
	{
		defaultLayout.components.clear();
		currentLayout = null;
	}

	public abstract void load(NBTTagCompound tagCompound);
	
	public abstract void save(NBTTagCompound tagCompound);
	
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
	
	public boolean isPendingLayoutUpdate()
	{
		return pendingLayoutUpdate;
	}
	
	public void clearPendingLayout() 
	{
		this.pendingLayoutUpdate = false;
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
	
	public ResourceLocation getIcon()
	{
		return icon;
	}
	
	public int getIconU()
	{
		return u;
	}
	
	public int getIconV()
	{
		return v;
	}
}
