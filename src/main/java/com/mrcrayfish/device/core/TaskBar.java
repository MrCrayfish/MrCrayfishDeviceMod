package com.mrcrayfish.device.core;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.SystemApplication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TaskBar
{
	public static final ResourceLocation APP_BAR_GUI = new ResourceLocation("cdm:textures/gui/application_bar.png");

	private static final int APPS_DISPLAYED = MrCrayfishDeviceMod.DEVELOPER_MODE ? 18 : 10;
	public static final int BAR_HEIGHT = 18;
	
	private Button btnLeft;
	private Button btnRight;
	
	private int offset = 0;

	private List<Application> applications;

	public TaskBar(List<Application> applications)
	{
		setupApplications(applications);
	}

	public void setupApplications(List<Application> applications)
	{
		this.applications = applications.stream().filter(app ->
		{
			if(app instanceof SystemApplication)
			{
				return true;
			}
			if(MrCrayfishDeviceMod.proxy.hasAllowedApplications())
			{
				if(MrCrayfishDeviceMod.proxy.getAllowedApplications().contains(app.getInfo()))
				{
					if(MrCrayfishDeviceMod.DEVELOPER_MODE)
					{
						return Settings.isShowAllApps();
					}
					return true;
				}
				return false;
			}
			else if(MrCrayfishDeviceMod.DEVELOPER_MODE)
			{
				return Settings.isShowAllApps();
			}
			return true;
		}).collect(Collectors.toList());

	}

	public void init(int posX, int posY)
	{
		btnLeft = new Button(0, 0, 12, 12, Icons.CHEVRON_LEFT);
		btnLeft.setPadding(1);
		btnLeft.xPosition = posX + 3;
		btnLeft.yPosition = posY + 3;
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
		btnRight = new Button(0, 0, 12, 12, Icons.CHEVRON_RIGHT);
		btnRight.setPadding(1);
		btnRight.xPosition = posX + 15 + 14 * APPS_DISPLAYED + 14;
		btnRight.yPosition = posY + 3;
		btnRight.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(offset + APPS_DISPLAYED < applications.size())
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
		RenderUtil.drawRectWithTexture(x + 1, y, 1, 0, Laptop.SCREEN_WIDTH - 34, 18, 1, 18);
		gui.drawTexturedModalRect(x + Laptop.SCREEN_WIDTH - 33, y, 2, 0, 33, 18);
		GlStateManager.disableBlend();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		btnLeft.render(gui, mc, btnLeft.xPosition, btnLeft.yPosition, mouseX, mouseY, true, partialTicks);
		btnRight.render(gui, mc, btnRight.xPosition, btnLeft.yPosition, mouseX, mouseY, true, partialTicks);

		for(int i = 0; i < APPS_DISPLAYED && i < applications.size(); i++)
		{
			AppInfo info = applications.get(i + offset).getInfo();
			RenderUtil.drawApplicationIcon(info, x + 18 + i * 16, y + 2);
			if(gui.isApplicationRunning(info.getFormattedId()))
			{
				mc.getTextureManager().bindTexture(APP_BAR_GUI);
				gui.drawTexturedModalRect(x + 17 + i * 16, y + 1, 35, 0, 16, 16);
			}
		}

		mc.fontRendererObj.drawString(timeToString(mc.player.world.getWorldTime()), x + 334, y + 5, Color.WHITE.getRGB(), true);
		
		mc.getTextureManager().bindTexture(APP_BAR_GUI);
		
		/* Settings App */
		gui.drawTexturedModalRect(x + 316, y + 2, 14, 30, 14, 14);
		gui.drawTexturedModalRect(x + 300, y + 2, 28, 30, 14, 14);

		/* Other Apps */
		if(isMouseInside(mouseX, mouseY, x + 18, y + 1, x + 236, y + 16))
		{
			int appIndex = (mouseX - x - 1) / 16 - 1 + offset;
			if(appIndex < offset + APPS_DISPLAYED && appIndex < applications.size())
			{
				gui.drawTexturedModalRect(x + (appIndex - offset) * 16 + 17, y + 1, 35, 0, 16, 16);
				gui.drawHoveringText(Collections.singletonList(applications.get(appIndex).getInfo().getName()), mouseX, mouseY);
			}
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
	}
	
	public void handleClick(Laptop laptop, int x, int y, int mouseX, int mouseY, int mouseButton) 
	{
		btnLeft.handleMouseClick(mouseX, mouseY, mouseButton);
		btnRight.handleMouseClick(mouseX, mouseY, mouseButton);

		if(isMouseInside(mouseX, mouseY, x + 18, y + 1, x + 236, y + 16))
		{
			int appIndex = (mouseX - x - 1) / 16 - 1 + offset;
			if(appIndex <= offset + APPS_DISPLAYED && appIndex < applications.size())
			{
				laptop.open(applications.get(appIndex));
				return;
			}
		}
	}
	
	public boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}

	public String timeToString(long time) 
	{
	    int hours = (int) ((Math.floor(time / 1000.0) + 7) % 24);
	    int minutes = (int) Math.floor((time % 1000) / 1000.0 * 60);
	    return String.format("%02d:%02d", hours, minutes);
	}
}
