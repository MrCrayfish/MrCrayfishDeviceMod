package com.mrcrayfish.device.app;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ApplicationBar 
{
	private static final ResourceLocation APP_BAR_GUI = new ResourceLocation("cdm:textures/gui/application_bar.png");
	
	private static final List<Application> APPS = new ArrayList<Application>();
	
	public static void registerApplication(Application app)
	{
		if(app != null)
		{
			APPS.add(app);
		}
	}
	
	public void render(Gui gui, Minecraft mc, int x, int y)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(APP_BAR_GUI);
		gui.drawTexturedModalRect(x, y, 0, 0, 238, 18);
		GlStateManager.disableBlend();
	}
}
