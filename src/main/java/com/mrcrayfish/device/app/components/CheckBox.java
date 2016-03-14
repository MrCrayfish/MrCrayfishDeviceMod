package com.mrcrayfish.device.app.components;

import java.awt.Color;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Layout;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class CheckBox extends Component
{
	public final String name;
	private boolean checked = false;
	private boolean enabled = true;
	
	public CheckBox(String name, int x, int y, int left, int top) 
	{
		super(x, y, left, top);
		this.name = name;
	}
	
	@Override
	public void render(Minecraft mc, int mouseX, int mouseY)
	{
		drawRect(xPosition, yPosition, xPosition + 10, yPosition + 10, Color.BLACK.getRGB());
		drawRect(xPosition + 1, yPosition + 1, xPosition + 9, yPosition + 9, Color.GRAY.getRGB());
		if(checked)
		{
			drawRect(xPosition + 2, yPosition + 2, xPosition + 8, yPosition + 8, Color.DARK_GRAY.getRGB());
		}
		drawString(mc.fontRendererObj, name, xPosition + 12, yPosition + 1, Color.WHITE.getRGB());
	}
	
	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton)
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
