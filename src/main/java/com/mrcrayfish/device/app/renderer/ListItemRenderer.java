package com.mrcrayfish.device.app.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class ListItemRenderer<E> 
{
	private final int height;
	
	public ListItemRenderer(int height) 
	{
		this.height = height;
	}
	
	public int getHeight() 
	{
		return height;
	}
	
	public abstract void render(E e, Gui gui, Minecraft mc, int x, int y, int width, boolean selected);
}
