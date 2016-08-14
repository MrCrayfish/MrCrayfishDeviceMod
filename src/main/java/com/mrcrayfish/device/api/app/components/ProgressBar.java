package com.mrcrayfish.device.api.app.components;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;

public class ProgressBar extends Component 
{
	public int width, height;
	
	private int progress = 0;
	private int max = 100;
	
	private int progressColour = new Color(189, 198, 255).getRGB();
	private int backgroundColour = Color.DARK_GRAY.getRGB();
	private int borderColour = Color.BLACK.getRGB();
	
	public ProgressBar(int x, int y, int left, int top, int width, int height)
	{
		super(x, y, left, top);
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
			drawRect(xPosition, yPosition, xPosition + width, yPosition + height, borderColour);
			drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, backgroundColour);
			drawRect(xPosition + 2, yPosition + 2, xPosition + 2 + (int) ((width - 4) * ((double) progress / (double) max)), yPosition + height - 2, progressColour);
        }
	}

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
	
	public int getProgress()
	{
		return progress;
	}
	
	public void setMax(int max) 
	{
		if(max > 0)
		{
			this.max = max;
		}
	}
}
