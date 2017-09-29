package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class ProgressBar extends Component 
{
	protected int width, height;
	protected int progress = 0;
	protected int max = 100;
	
	protected int progressColour = new Color(189, 198, 255).getRGB();
	protected int backgroundColour = Color.DARK_GRAY.getRGB();
	protected int borderColour = Color.BLACK.getRGB();
	
	/**
	 * Default progress bar constructor
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width width of the progress bar
	 * @param height height of the progress bar
	 */
	public ProgressBar(int left, int top, int width, int height)
	{
		super(left, top);
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		if (this.visible)
        {
			drawRect(xPosition, yPosition, xPosition + width, yPosition + height, borderColour);
			drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, backgroundColour);
			drawRect(xPosition + 2, yPosition + 2, xPosition + 2 + getProgressScaled(), yPosition + height - 2, progressColour);
        }
	}
	
	private int getProgressScaled() 
	{
		return (int) Math.ceil(((width - 4) * ((double) progress / (double) max)));
	}
	
	/**
	 * Sets the current progress.
	 * 
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) 
	{
		if(progress > max)
		{
			progress = max;
		}
		else if(progress < 0)
		{
			progress = 0;
		}
		this.progress = progress;
	}
	
	/**
	 * Gets the current progress.
	 * 
	 * @return the progress
	 */
	public int getProgress()
	{
		return progress;
	}
	
	/**
	 * Sets the max progress
	 * 
	 * @param max the max progress
	 */
	public void setMax(int max) 
	{
		if(max > 0)
		{
			this.max = max;
		}
	}
}
