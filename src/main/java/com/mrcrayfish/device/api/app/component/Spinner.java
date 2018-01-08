package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Spinner extends Component 
{
	protected final int MAX_PROGRESS = 31;
	protected int currentProgress = 0;
	
	protected int spinnerColour = -1;
	
	/**
	 * Default spinner constructor
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 */
	public Spinner(int left, int top)
	{
		super(left, top);
	}
	
	@Override
	public void handleTick() 
	{
		if(currentProgress >= MAX_PROGRESS)
		{
			currentProgress = 0;
		}
		currentProgress++;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			Color bgColor = new Color(color(spinnerColour, getColourScheme().getBackgroundColour())).brighter().brighter();
			float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
			bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], 1.0F));
			GL11.glColor4f(bgColor.getRed() / 255F, bgColor.getGreen() / 255F, bgColor.getBlue() / 255F, 1.0F);
			mc.getTextureManager().bindTexture(Component.COMPONENTS_GUI);
			drawTexturedModalRect(xPosition, yPosition, (currentProgress % 8) * 12, 12 + 12 * (int) Math.floor((double) currentProgress / 8), 12, 12);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
	}
}
