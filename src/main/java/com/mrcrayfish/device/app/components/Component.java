package com.mrcrayfish.device.app.components;

import java.util.List;

import com.mrcrayfish.device.gui.GuiLaptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;

public abstract class Component 
{
	public void init(List<GuiButton> buttons, int x, int y) {};
	
	public void render(GuiLaptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY) {};
	
	public void handleButtonClick(GuiLaptop laptop, GuiButton button) {};
	
	public void handleClick(GuiLaptop gui, int x, int y, int mouseX, int mouseY, int mouseButton) {};
	
	public void handleKeyTyped(char character, int code) {};
	
	public void handleDrag(GuiScreen gui, int x, int y, int mouseDX, int mouseDY, int screenStartX, int screenStartY) {};
	
	public void handleClose(List<GuiButton> buttons) {};
	
	public void updateComponents(int x, int y) {};
	
	public boolean save(NBTTagCompound tagCompound) 
	{
		return false;
	}
}
