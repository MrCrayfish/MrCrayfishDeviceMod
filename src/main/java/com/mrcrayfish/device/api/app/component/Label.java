package com.mrcrayfish.device.api.app.component;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;

public class Label extends Component {

	protected String text;
	protected int width;
	protected boolean shadow = true;

	protected int textColour = Color.WHITE.getRGB();
	
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
			mc.fontRendererObj.drawString(text, xPosition, yPosition, textColour, shadow);
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
	
	public void setShadow(boolean shadow)
	{
		this.shadow = shadow;
	}
}
