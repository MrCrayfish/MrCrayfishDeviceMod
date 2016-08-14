package com.mrcrayfish.device.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.components.Button;
import com.mrcrayfish.device.api.app.components.ButtonArrow;
import com.mrcrayfish.device.api.app.components.ButtonArrow.Type;
import com.mrcrayfish.device.api.app.listeners.ClickListener;
import com.mrcrayfish.device.programs.system.ApplicationAppStore;
import com.mrcrayfish.device.programs.system.ApplicationSettings;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

public class TaskBar
{
	public static final ResourceLocation APP_BAR_GUI = new ResourceLocation("cdm:textures/gui/application_bar.png");
	
	private static final List<Application> APPS = new ArrayList<Application>();
	
	private static Application settings = new ApplicationSettings(); 
	private static Application app_store = new ApplicationAppStore(); 
	
	private static final int APPS_DISPLAYED = 10;
	public static final int BAR_HEIGHT = 18;
	
	private Button btnLeft;
	private Button btnRight;
	
	private int offset = 0;

	public void init(int posX, int posY)
	{
		btnLeft = new ButtonArrow(posX, posY, 3, 3, Type.LEFT);
		btnLeft.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(offset > 0)
				{
					offset--;
				}
			}
		});
		btnRight = new ButtonArrow(posX, posY, 15 + 14 * APPS_DISPLAYED + 14, 3, Type.RIGHT);
		btnRight.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(offset + APPS_DISPLAYED < APPS.size())
				{
					offset++;
				}
			}
		});
	}
	
	public void render(Laptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY, float partialTicks)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(APP_BAR_GUI);
		gui.drawTexturedModalRect(x, y, 0, 0, 1, 18);
		GuiHelper.drawModalRectWithUV(x + 1, y, 1, 0, gui.SCREEN_WIDTH - 34, 18, 1, 18);
		gui.drawTexturedModalRect(x + gui.SCREEN_WIDTH - 33, y, 2, 0, 33, 18);
		GlStateManager.disableBlend();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		btnLeft.render(gui, mc, mouseX, mouseY, true, partialTicks);
		btnRight.render(gui, mc, mouseX, mouseY, true, partialTicks);

		for(int i = 0; i < APPS_DISPLAYED && i < APPS.size(); i++)
		{
			Application app = APPS.get(i + offset);
			if(app.getIcon() != null)
			{
				mc.getTextureManager().bindTexture(app.getIcon());
				gui.drawTexturedModalRect(x + 18 + i * 16, y + 2, app.getIconU(), app.getIconV(), 14, 14);
			}
			else
			{
				mc.getTextureManager().bindTexture(APP_BAR_GUI);
				gui.drawTexturedModalRect(x + 18 + i * 16, y + 2, 0, 30, 14, 14);
			}
			
			if(gui.isAppActive(app.getID())) 
			{
				gui.drawTexturedModalRect(x + 17 + i * 16, y + 1, 35, 0, 16, 16);
			}
		}
		
		
		mc.fontRendererObj.drawString(timeToString(mc.thePlayer.worldObj.getWorldTime()), x + 334, y + 5, Color.WHITE.getRGB(), true);
		
		mc.getTextureManager().bindTexture(APP_BAR_GUI);
		
		/* Settings App */
		gui.drawTexturedModalRect(x + 316, y + 2, 14, 30, 14, 14);
		gui.drawTexturedModalRect(x + 300, y + 2, 28, 30, 14, 14);
		
		if(isMouseInside(mouseX, mouseY, x + 316, y + 1, x + 330, y + 16))
		{
			gui.drawTexturedModalRect(x + 315, y + 1, 35, 0, 16, 16);
			gui.drawHoveringText(Arrays.asList(settings.getDisplayName()), mouseX, mouseY);
		}
		
		if(isMouseInside(mouseX, mouseY, x + 300, y + 1, x + 314, y + 16))
		{
			gui.drawTexturedModalRect(x + 299, y + 1, 35, 0, 16, 16);
			gui.drawHoveringText(Arrays.asList(app_store.getDisplayName()), mouseX, mouseY);
		}
		
		/* Other Apps */
		if(isMouseInside(mouseX, mouseY, x + 18, y + 1, x + 236, y + 16))
		{
			int appIndex = (mouseX - x - 1) / 16 - 1 + offset;
			if(appIndex < offset + APPS_DISPLAYED && appIndex < APPS.size())
			{
				gui.drawTexturedModalRect(x + (appIndex - offset) * 16 + 17, y + 1, 35, 0, 16, 16);
				gui.drawHoveringText(Arrays.asList(APPS.get(appIndex).getDisplayName()), mouseX, mouseY);
			}
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
	}
	
	public void handleClick(Laptop gui, int x, int y, int mouseX, int mouseY, int mouseButton) 
	{
		btnLeft.handleClick(null, mouseX, mouseY, mouseButton);
		btnRight.handleClick(null, mouseX, mouseY, mouseButton);
		
		if(isMouseInside(mouseX, mouseY, x + 315, y + 1, x + 331, y + 16))
		{
			gui.openApplication(settings);
			return;
		}
		
		if(isMouseInside(mouseX, mouseY, x + 299, y + 1, x + 315, y + 16))
		{
			gui.openApplication(app_store);
			return;
		}
		
		if(isMouseInside(mouseX, mouseY, x + 18, y + 1, x + 236, y + 16))
		{
			int appIndex = (mouseX - x - 1) / 16 - 1 + offset;
			if(appIndex <= offset + APPS_DISPLAYED && appIndex < APPS.size())
			{
				gui.openApplication(APPS.get(appIndex));
				return;
			}
		}
	}
	
	public boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}
	
	public static void registerApplication(Application app)
	{
		if(app != null)
		{
			APPS.add(app);
		}
	}
	
	public String timeToString(long time) 
	{
	    int hours = (int) ((Math.floor(time / 1000.0) + 7) % 24);
	    int minutes = (int) Math.floor((time % 1000) / 1000.0 * 60);
	    return String.format("%02d:%02d", hours, minutes);
	}
}
