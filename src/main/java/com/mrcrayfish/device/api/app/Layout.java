package com.mrcrayfish.device.api.app;

import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.api.app.listener.InitListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Layout
{
	public List<Component> components;
	public int width;
	public int height;
	
	private InitListener initListener;
	private Background background;
	
	public Layout() 
	{
		this(200, 100);
	}
	
	public Layout(int width, int height)
	{
		this.components = new ArrayList<Component>();
		this.width = width;
		this.height = height;
	}
	
	protected void init()
	{
		if(initListener != null)
		{
			initListener.onInit();
		}
	}
	
	public void addComponent(Component c)
	{
		if(c != null)
		{
			this.components.add(c);
			c.init(this);
		}
	}
	
	public List<Component> getComponents()
	{
		return components;
	}
	
	public void render(Gui gui, Minecraft mc, int x, int y)
	{
		if(background != null)
		{
			background.render(gui, mc, x, y);
		}
	}
	
	public void setInitListener(InitListener initListener)
	{
		this.initListener = initListener;
	}
	
	public void setBackground(Background background) 
	{
		this.background = background;
	}
	
	public void clear() 
	{	
		this.components.clear();
	}
	
	public interface Background
	{
		public void render(Gui gui, Minecraft mc, int x, int y);
	}
}
