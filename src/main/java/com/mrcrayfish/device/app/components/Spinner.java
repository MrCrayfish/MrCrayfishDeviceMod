package com.mrcrayfish.device.app.components;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.app.Component;

import net.minecraft.client.Minecraft;

public class Spinner extends Component 
{
	public int progress = 0;
	
	public Spinner(int x, int y, int left, int top)
	{
		super(x, y, left, top);
	}
	
	@Override
	public void handleTick() 
	{
		if(progress >= 31)
		{
			progress = 0;
		}
		progress++;
		
		System.out.println("ticking");
	}

	@Override
	public void render(Minecraft mc, int mouseX, int mouseY) 
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(Component.COMPONENTS_GUI);
		drawTexturedModalRect(xPosition, yPosition, (progress % 8) * 12, 12 + 12 * (int) Math.floor((double) progress / 8), 12, 12);
		System.out.println(progress);
	}
}
