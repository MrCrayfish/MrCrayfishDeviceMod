package com.mrcrayfish.device.object;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Slider;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;

public class ColourGrid extends Component 
{
	private static Color[] colours = { Color.BLACK, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE, Color.RED, Color.ORANGE, Color.YELLOW,
			Color.GREEN, new Color(0, 150, 0), new Color(0, 255, 255), new Color(0, 148, 255), Color.BLUE, new Color(72, 0, 255), Color.MAGENTA, new Color(255, 0, 110) };
	
	private int hoverColour = new Color(255, 255, 255, 100).getRGB();
	
	private Canvas canvas;
	private Slider redSlider;
	private Slider greenSlider;
	private Slider blueSlider;

	private int width;
	
	public ColourGrid(int left, int top, int width, Canvas canvas, Slider redSlider, Slider greenSlider, Slider blueSlider) 
	{
		super(left, top);
		this.width = width;
		this.canvas = canvas;
		this.redSlider = redSlider;
		this.greenSlider = greenSlider;
		this.blueSlider = blueSlider;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		int endX = xPosition + width + 2;
		int endY = yPosition + (colours.length / 5) * 10 + 2;
		drawRect(xPosition, yPosition, endX, endY, Color.DARK_GRAY.getRGB());
		for(int i = 0; i < colours.length; i++)
		{
			int startX = xPosition + (i % 5) * 10 + 1;
			int startY = yPosition + (i / 5) * 10 + 1;
			drawRect(startX, startY, startX + 10, startY + 10, colours[i].getRGB());
		}
		
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition + 1, yPosition + 1, endX - 2, endY - 2))
		{
			int boxX = (mouseX - xPosition - 1) / 10;
			int boxY = (mouseY - yPosition - 1) / 10;
			drawRect(xPosition + (boxX * 10) + 1, yPosition + (boxY * 10) + 1, xPosition + (boxX * 10) + 11, yPosition + (boxY * 10) + 11, hoverColour);
		}
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) 
	{
		int endX = xPosition + width + 2;
		int endY = yPosition + (colours.length / 5) * 10 + 2;
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition + 1, yPosition + 1, endX - 2, endY - 2))
		{
			int boxX = (mouseX - xPosition - 1) / 10;
			int boxY = (mouseY - yPosition - 1) / 10;
			int index = boxX + boxY * 5;
			redSlider.setPercentage(colours[index].getRed() / 255F);
			greenSlider.setPercentage(colours[index].getGreen() / 255F);
			blueSlider.setPercentage(colours[index].getBlue() / 255F);
			canvas.setRed(redSlider.getPercentage());
			canvas.setGreen(greenSlider.getPercentage());
			canvas.setBlue(blueSlider.getPercentage());
		}
	}

}
