package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Icons;

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
		LEFT(Icons.CHEVRON_LEFT),
		UP(Icons.CHEVRON_UP),
		RIGHT(Icons.CHEVRON_RIGHT),
		DOWN(Icons.CHEVRON_DOWN);
		
		private Icons icon;
		Type(Icons icon)
		{
			this.icon = icon;
		}
	}
}
