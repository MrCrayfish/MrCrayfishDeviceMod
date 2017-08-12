package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Spinner extends Component 
{
	protected final int MAX_PROGRESS = 31;
	protected int currentProgress = 0;
	
	protected Color spinnerColour = Color.WHITE;
	
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
			GL11.glColor4f(spinnerColour.getRed() / 255F, spinnerColour.getGreen() / 255F, spinnerColour.getBlue() / 255F, spinnerColour.getAlpha() / 255F);
			mc.getTextureManager().bindTexture(Component.COMPONENTS_GUI);
			drawTexturedModalRect(xPosition, yPosition, (currentProgress % 8) * 12, 12 + 12 * (int) Math.floor((double) currentProgress / 8), 12, 12);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
	}
}
