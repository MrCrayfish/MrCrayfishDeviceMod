package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.List;

public class Text extends Component 
{
	protected List<String> lines;
	protected int width;
	protected boolean shadow = false;
	
	protected int textColor = Color.WHITE.getRGB();
	
	/**
	 * Default text constructor
	 * 
	 * @param text the text to display
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width the max width
	 */
	public Text(String text, int left, int top, int width) 
	{
		super(left, top);
		this.width = width;
		this.setText(text);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
			for(int i = 0; i < lines.size(); i++)
			{
				Laptop.fontRenderer.drawString(lines.get(i), xPosition, yPosition + (i * 10), textColor, shadow);
			}
        }
	}
	
	/**
	 * Sets the text for this component 
	 * 
	 * @param text the text
	 */
	public void setText(String text)
	{
		this.lines = Laptop.fontRenderer.listFormattedStringToWidth(text, width);
	}
	
	/**
	 * Sets the text color for this component
	 * 
	 * @param color the text color
	 */
	public void setTextColor(Color color)
	{
		this.textColor = color.getRGB();
	}
	
	/**
	 * Sets the whether shadow should show under the text
	 * 
	 * @param shadow the text color
	 */
	public void setShadow(boolean shadow)
	{
		this.shadow = shadow;
	}
}
