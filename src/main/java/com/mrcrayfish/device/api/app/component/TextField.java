package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Application;

import net.minecraft.client.gui.FontRenderer;

public class TextField extends TextArea {

	/**
	 * Default text field constructor
	 * 
	 * @param x the application x position (from {@link Application#init(int x, int y)})
	 * @param y the application y position (from {@link Application#init(int x, int y)})
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width the width of the text field
	 */
	public TextField(int x, int y, int left, int top, int width) 
	{
		super(x, y, left, top, width, 15);
	}
}
