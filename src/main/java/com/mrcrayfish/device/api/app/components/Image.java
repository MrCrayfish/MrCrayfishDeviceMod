package com.mrcrayfish.device.api.app.components;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Image extends Component 
{
	private ResourceLocation image;
	private int imageU, imageV;
	private int imageWidth, imageHeight;
	private int componentWidth, componentHeight;

	private float alpha = 1.0F;
	
	private boolean hasBorder = false;
	private int borderColour = Color.BLACK.getRGB();
	private int borderThickness = 1;
	
	
	public Image(int x, int y, int left, int top, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource) 
	{
		this(x, y, left, top, imageWidth, imageHeight, imageU, imageV, imageWidth, imageHeight, resource);
	}
	
	public Image(int x, int y, int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource) 
	{
		super(x, y, left, top);
		this.image = resource;
		this.componentWidth = componentWidth;
		this.componentHeight = componentHeight;
		this.imageU = imageU;
		this.imageV = imageV;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			mc.getTextureManager().bindTexture(image);
			
			if(hasBorder)
			{
				drawRect(xPosition, yPosition, xPosition + componentWidth, yPosition + componentHeight, borderColour);
				GuiHelper.drawModalRectWithUV(xPosition + borderThickness, yPosition + borderThickness, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, imageWidth, imageHeight);
			}
			else
			{
				GuiHelper.drawModalRectWithUV(xPosition, yPosition, imageU, imageV, componentWidth, componentHeight, imageWidth, imageHeight);
			}
        }
	}
	
	public void setAlpha(float alpha) 
	{
		if(alpha < 0.0F)
		{
			this.alpha = 0.0F;
			return;
		}
		if(alpha > 1.0F)
		{
			this.alpha = 1.0F;
			return;
		}
		this.alpha = alpha;
	}
	
	public void showBorder(boolean show)
	{
		this.hasBorder = show;
	}
	
	private void setBorderColor(Color colour)
	{
		this.borderColour = colour.getRGB();
	}
	
	public void setBorderThickness(int thickness)
	{
		this.borderThickness = thickness;
	}
}
