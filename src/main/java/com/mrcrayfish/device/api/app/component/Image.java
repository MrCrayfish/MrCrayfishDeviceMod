package com.mrcrayfish.device.api.app.component;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Application;
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
	
	/**
	 * Default image constructor
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param imageU the image u position on the resource 
	 * @param imageV the image v position on the resource
	 * @param imageWidth the image width
	 * @param imageHeight the image height
	 * @param resource the resource location of the image
	 */
	public Image(int left, int top, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource) 
	{
		this(left, top, imageWidth, imageHeight, imageU, imageV, imageWidth, imageHeight, resource);
	}
	
	/**
	 * The alternate constructor to specify a custom width and height
	 * for the image.
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param componentWidth the width of the component
	 * @param componentHeight the height of the component
	 * @param imageU the image u position on the resource 
	 * @param imageV the image v position on the resource
	 * @param imageWidth the image width
	 * @param imageHeight the image height
	 * @param resource the resource location of the image
	 */
	public Image(int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource) 
	{
		super(left, top);
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
	
	/**
	 * Sets the alpha for this image. Must be in the range
	 * of 0.0F to 1.0F
	 * 
	 * @param alpha how transparent you want it to be.
	 */
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
	
	/**
	 * Makes it so the border shows
	 * 
	 * @param show should the border show
	 */
	public void setBorderVisible(boolean show)
	{
		this.hasBorder = show;
	}
	
	/**
	 * Sets the border colour for this component
	 * 
	 * @param color the border colour
	 */
	private void setBorderColor(Color colour)
	{
		this.borderColour = colour.getRGB();
	}
	
	/**
	 * Sets the thickness of the border
	 * 
	 * @param thickness how thick in pixels
	 */
	public void setBorderThickness(int thickness)
	{
		this.borderThickness = thickness;
	}
}
