package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Application;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class ButtonToggle extends Button implements RadioGroup.Item
{
	protected boolean toggle = false;
	protected RadioGroup group = null;
	
	/**
	 * Default toggle button constructor
	 * 
	 * @param text text to be displayed in the button
	 * @param x the application x position (from {@link Application#init(int x, int y)}).
	 * @param y the application y position (from {@link Application#init(int x, int y)}).
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width width of the button
	 * @param height height of the button
	 */
	public ButtonToggle(String text, int left, int top, int width, int height) 
	{
		super(text, left, top, width, height);
	}
	
	/**
	 * Creates a toggle button with an image inside. The size of the 
	 * button is based on the size of the image with 3 pixels of padding.
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param icon the icon resource location
	 * @param iconU the u position on the resource
	 * @param iconV the v position on the resource
	 * @param iconWidth width of the icon
	 * @param iconHeight height of the icon
	 */
	public ButtonToggle(int left, int top, ResourceLocation icon, int iconU, int iconV, int iconWidth, int iconHeight)
	{
		super(left, top, icon, iconU, iconV, iconWidth, iconHeight);
	}
	
	/**
	 * Sets the radio group for this button.
	 * 
	 * @param group the radio group.
	 */
	public void setRadioGroup(RadioGroup group)
	{
		this.group = group;
		this.group.add(this);
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) 
	{
		if(!this.visible || !this.enabled)
			return;
		
		if(this.hovered)
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
