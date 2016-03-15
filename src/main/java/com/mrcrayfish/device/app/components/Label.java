package com.mrcrayfish.device.app.components;

import java.awt.Color;
import java.util.List;

import com.mrcrayfish.device.app.Component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Label extends Component {

	private String text;
	private int width;
	
	private int textColour = Color.WHITE.getRGB();
	
	public Label(String text, int x, int y, int left, int top) 
	{
		super(x, y, left, top);
		this.text = text;
	}

	@Override
	public void render(Minecraft mc, int mouseX, int mouseY, boolean windowActive) 
	{
		if (this.visible)
        {
			mc.fontRendererObj.drawString(text, xPosition, yPosition, textColour, true);
        }
	}
	
	public void setText(String text) 
	{
		this.text = text;
	}
	
	public void setTextColour(Color color) 
	{
		this.textColour = color.getRGB();
	}
}
