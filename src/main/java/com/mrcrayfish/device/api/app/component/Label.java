package com.mrcrayfish.device.api.app.component;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class Label extends Component {

	protected String text;
	protected int width;
	protected boolean shadow = true;
	protected double scale = 1;

	protected int textColour = Color.WHITE.getRGB();
	
	/**
	 * Default label constructor
	 * 
	 * @param text the text to display
	 * @param x the application x position (from {@link Application#init(int x, int y)}).
	 * @param y the application y position (from {@link Application#init(int x, int y)}).
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 */
	public Label(String text, int x, int y, int left, int top) 
	{
		super(x, y, left, top);
		this.text = text;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
			GlStateManager.pushMatrix();
			GlStateManager.translate(xPosition, yPosition, 0);
			GlStateManager.scale(scale, scale, scale);
			mc.fontRendererObj.drawString(text, 0, 0, textColour, shadow);
			GlStateManager.popMatrix();
        }
	}
	
	/**
	 * Sets the text in the label
	 * 
	 * @param text the text
	 */
	public void setText(String text) 
	{
		this.text = text;
	}
	
	/**
	 * Sets the text colour for this component
	 * 
	 * @param color the text colour
	 */
	public void setTextColour(Color color) 
	{
		this.textColour = color.getRGB();
	}
	
	/**
	 * Sets the whether shadow should show under the text
	 * 
	 * @param color the text colour
	 */
	public void setShadow(boolean shadow)
	{
		this.shadow = shadow;
	}
	
	/**
	 * Scales the text, essentially setting the font size. Minecraft
	 * does not support proper font resizing. The default scale is 1
	 * 
	 * @param scale the text scale
	 */
	public void setScale(double scale)
	{
		this.scale = scale;
	}
}
