package com.mrcrayfish.device.api.app.component;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.ReleaseListener;
import com.mrcrayfish.device.api.app.listener.SlideListener;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;

public class Slider extends Component
{
	protected boolean dragging = false;
	protected int clickX;
	
	protected int width;
	protected int prevSliderX;
	protected int newSliderX;
	
	protected int sliderColour = Color.WHITE.getRGB();
	protected int backgroundColour = Color.DARK_GRAY.getRGB();
	protected int borderColour = Color.BLACK.getRGB();
	
	protected ClickListener clickListener = null;
	protected ReleaseListener releaseListener = null;
	protected SlideListener slideListener = null;
	
	public Slider(int x, int y, int left, int top, int width) 
	{
		super(x, y, left, top);
		this.width = width;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
			drawRect(xPosition, yPosition + 4, xPosition + width, yPosition + 8, borderColour);
			drawRect(xPosition + 1, yPosition + 5, xPosition + width - 1, yPosition + 7, backgroundColour);
			drawRect(xPosition + newSliderX, yPosition, xPosition + newSliderX + 8, yPosition + 12, borderColour);
			drawRect(xPosition + newSliderX + 1, yPosition + 1, xPosition + newSliderX + 7, yPosition + 11, sliderColour);
        }
	}
	
	@Override
	public void handleClick(int mouseX, int mouseY, int mouseButton) 
	{
		if(!this.visible || !this.enabled)
			return;
		
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition + prevSliderX, yPosition, xPosition + prevSliderX + 8, yPosition + 12))
		{
			this.dragging = true;
			this.clickX = mouseX;
			if(clickListener != null)
			{
				clickListener.onClick(this, mouseButton);
			}
		}
	}
	
	@Override
	public void handleDrag(int mouseX, int mouseY, int mouseButton) 
	{
		if(dragging)
		{
			this.newSliderX = prevSliderX + (mouseX - clickX);
			if(this.newSliderX < 0)
			{
				this.newSliderX = 0;
			}
			if(this.newSliderX >= width - 8)
			{
				this.newSliderX = width - 8;
			}
			if(slideListener != null)
			{
				slideListener.onSlide(getPercentage());
			}
		}
	}
	
	@Override
	public void handleRelease(int mouseX, int mouseY, int mouseButton) 
	{
		this.dragging = false;
		this.prevSliderX = this.newSliderX;
		if(releaseListener != null)
		{
			releaseListener.onRelease(this, 0);
		}
	}
	
	public void setClickListener(ClickListener clickListener) 
	{
		this.clickListener = clickListener;
	}
	
	public void setReleaseListener(ReleaseListener releaseListener) 
	{
		this.releaseListener = releaseListener;
	}
	
	public void setSlideListener(SlideListener slideListener) 
	{
		this.slideListener = slideListener;
	}
	
	public float getPercentage()
	{
		return (float) this.newSliderX / (float) (this.width - 8);
	}
	
	public void setPercentage(float percentage)
	{
		this.newSliderX = (int) ((this.width - 8) * percentage);
	}
	
	public void setSliderColour(Color color) 
	{
		this.sliderColour = color.getRGB();
	}
	
	public void setBackgroundColour(Color color) 
	{
		this.backgroundColour = color.getRGB();
	}
	
	public void setBorderColour(Color color) 
	{
		this.borderColour = color.getRGB();
	}
}
