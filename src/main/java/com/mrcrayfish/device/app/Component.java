package com.mrcrayfish.device.app;

import java.util.List;

import com.mrcrayfish.device.app.components.Button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public abstract class Component extends Gui
{
	public int xPosition, yPosition;
	int left, top;
	public boolean enabled = true;
	public boolean visible = true;
	
	public Component(int x, int y, int left, int top) 
	{
		this.xPosition = x + left;
		this.yPosition = y + top;
		this.left = left;
		this.top = top;
	}
	
	public void init(Application app) {};
	
	public abstract void render(Minecraft mc, int mouseX, int mouseY);
	
	public void handleButtonClick(Button button) {};
	
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton) {};
	
	public void handleKeyTyped(char character, int code) {};
	
	public void handleDrag(int mouseDX, int mouseDY, int screenStartX, int screenStartY) {};
	
	public void updateComponents(int x, int y) {};
	
	public boolean save(NBTTagCompound tagCompound) 
	{
		return false;
	}
}
