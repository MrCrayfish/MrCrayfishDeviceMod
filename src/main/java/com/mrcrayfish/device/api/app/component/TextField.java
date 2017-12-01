package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class TextField extends TextArea
{
	private IIcon icon;

	/**
	 * Default text field constructor
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width the width of the text field
	 */
	public TextField(int left, int top, int width) 
	{
		super(left, top, width, 16);
		this.setScrollBarVisible(false);
		this.setMaxLines(1);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		if(icon != null)
		{
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			Gui.drawRect(x, y, x + 15, y + 16, borderColour);
			Gui.drawRect(x + 1, y + 1, x + 15, y + 15, secondaryBackgroundColour);
			icon.draw(mc, x + 3, y + 3);
		}
		super.render(laptop, mc, x + (icon != null ? 15 : 0), y, mouseX, mouseY, windowActive, partialTicks);
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		super.handleMouseClick(mouseX - (icon != null ? 15 : 0), mouseY, mouseButton);
	}

	@Override
	protected void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
	{
		super.handleMouseDrag(mouseX - (icon != null ? 15 : 0), mouseY, mouseButton);
	}

	@Override
	protected void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
	{
		super.handleMouseRelease(mouseX - (icon != null ? 15 : 0), mouseY, mouseButton);
	}

	public void setIcon(IIcon icon)
	{
		if(this.icon == null)
		{
			width -= 15;
		}
		else if(icon == null)
		{
			width += 15;
		}
		this.icon = icon;
	}
}
