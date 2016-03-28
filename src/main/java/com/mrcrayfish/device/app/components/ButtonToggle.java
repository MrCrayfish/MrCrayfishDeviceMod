package com.mrcrayfish.device.app.components;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class ButtonToggle extends Button implements IRadioGroupItem
{
	private boolean toggle = false;
	private RadioGroup group = null;
	
	public ButtonToggle(String text, int x, int y, int left, int top, int width, int height) 
	{
		super(text, x, y, left, top, width, height);
	}
	
	public ButtonToggle(int x, int y, int left, int top, ResourceLocation icon, int iconU, int iconV, int iconWidth, int iconHeight)
	{
		super(x, y, left, top, icon, iconU, iconV, iconWidth, iconHeight);
	}
	
	public void setRadioGroup(RadioGroup group)
	{
		this.group = group;
		this.group.add(this);
	}
	
	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton) 
	{
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + height))
		{
			if(clickListener != null)
			{
				clickListener.onClick(this, mouseButton);
			}
			playClickSound(Minecraft.getMinecraft().getSoundHandler());
			if(group != null)
			{
				group.unselect();
			}
			this.toggle = true;
		}
	}
	
	@Override
	public boolean isInside(int mouseX, int mouseY) 
	{
		return super.isInside(mouseX, mouseY) || toggle;
	}

	@Override
	public void setSelected(boolean selected) 
	{
		this.toggle = selected;
	}

	@Override
	public boolean isSelected() 
	{
		return toggle;
	}
}
