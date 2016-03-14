package com.mrcrayfish.device.app.components;

import java.awt.Color;
import java.util.List;

import com.mrcrayfish.device.app.Component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Text extends Component {

	private List<String> lines;
	private int width;
	
	private int textColour = Color.WHITE.getRGB();
	
	public Text(String text, FontRenderer renderer, int x, int y, int left, int top, int width) 
	{
		super(x, y, left, top);
		this.lines = renderer.listFormattedStringToWidth(text, width);
		this.width = width;
	}

	@Override
	public void render(Minecraft mc, int mouseX, int mouseY) 
	{
		for(int i = 0; i < lines.size(); i++)
		{
			mc.fontRendererObj.drawString(lines.get(i), xPosition, yPosition + (i * 10), textColour, true);
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
}
