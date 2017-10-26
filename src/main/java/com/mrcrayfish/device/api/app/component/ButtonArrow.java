package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icon;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class ButtonArrow extends Button
{
	/**
	 * Creates an arrow button
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param type the arrow type
	 */
	public ButtonArrow(int left, int top, Type type) 
	{
		super(left, top, type.icon);
		this.setSize(12, 12);
	}

	public enum Type
	{
		LEFT(Icon.CHEVRON_LEFT),
		UP(Icon.CHEVRON_UP),
		RIGHT(Icon.CHEVRON_RIGHT),
		DOWN(Icon.CHEVRON_DOWN);
		
		private Icon icon;
		Type(Icon icon)
		{
			this.icon = icon;
		}
	}
}
