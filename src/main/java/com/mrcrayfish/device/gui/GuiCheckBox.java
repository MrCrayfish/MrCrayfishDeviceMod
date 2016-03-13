package com.mrcrayfish.device.gui;

import java.awt.Color;

import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiCheckBox extends Gui 
{
	public final String name;
	public int xPosition, yPosition;
	private boolean checked = false;
	private boolean enabled = true;
	
	public GuiCheckBox(String name, int x, int y) 
	{
		this.name = name;
		this.xPosition = x;
		this.yPosition = y;
	}
	
	public void render(Minecraft mc)
	{
		drawRect(xPosition, yPosition, xPosition + 10, yPosition + 10, Color.BLACK.getRGB());
		drawRect(xPosition + 1, yPosition + 1, xPosition + 9, yPosition + 9, Color.GRAY.getRGB());
		if(checked)
		{
			drawRect(xPosition + 2, yPosition + 2, xPosition + 8, yPosition + 8, Color.DARK_GRAY.getRGB());
		}
		drawString(mc.fontRendererObj, name, xPosition + 12, yPosition + 1, Color.WHITE.getRGB());
	}
	
	public void onMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + 10, yPosition + 10))
		{
			this.checked = !checked;
		}
	}
	
	public boolean isChecked() 
	{
		return checked;
	}
	
	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}
}
