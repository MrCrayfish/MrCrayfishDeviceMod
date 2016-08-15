package com.mrcrayfish.device.api.app.component;

import java.awt.Color;
import java.util.List;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Text extends Component 
{
	protected List<String> lines;
	protected int width;
	protected boolean shadow = false;
	
	protected int textColour = Color.WHITE.getRGB();
	
	public Text(String text, FontRenderer renderer, int x, int y, int left, int top, int width) 
	{
		super(x, y, left, top);
		this.lines = renderer.listFormattedStringToWidth(text, width);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
			for(int i = 0; i < lines.size(); i++)
			{
				mc.fontRendererObj.drawString(lines.get(i), xPosition, yPosition + (i * 10), textColour, shadow);
			}
        }
	}
	
	public void setText(String text)
	{
		this.lines = Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(text, width);
	}
	
	public void setTextColour(Color color) 
	{
		this.textColour = color.getRGB();
	}
	
	public void setShadow(boolean shadow)
	{
		this.shadow = shadow;
	}
}
