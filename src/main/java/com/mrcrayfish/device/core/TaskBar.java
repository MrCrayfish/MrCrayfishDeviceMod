package com.mrcrayfish.device.core;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.network.TrayItemWifi;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.object.TrayItem;
import com.mrcrayfish.device.programs.system.SystemApplication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskBar
{
	public static final ResourceLocation APP_BAR_GUI = new ResourceLocation("cdm:textures/gui/application_bar.png");

	private static final int APPS_DISPLAYED = MrCrayfishDeviceMod.DEVELOPER_MODE ? 18 : 10;
	public static final int BAR_HEIGHT = 18;
	
	private Button btnLeft;
	private Button btnRight;
	
	private int offset = 0;
	private int pingTimer = 0;

	private List<Application> applications;
	private List<TrayItem> trayItems = new ArrayList<>();

	public TaskBar(List<Application> applications)
	{
		setupApplications(applications);
		trayItems.add(new TrayItemWifi());
	}

	public void init()
	{
		trayItems.forEach(TrayItem::init);
	}

	public void setupApplications(List<Application> applications)
	{
		final Predicate<Application> VALID_APPS = app ->
		{
			if(app instanceof SystemApplication)
			{
				return true;
			}
			if(MrCrayfishDeviceMod.proxy.hasAllowedApplications())
			{
				if(MrCrayfishDeviceMod.proxy.getAllowedApplications().contains(app.getInfo()))
				{
					return !MrCrayfishDeviceMod.DEVELOPER_MODE || Settings.isShowAllApps();
				}
				return false;
			}
			else if(MrCrayfishDeviceMod.DEVELOPER_MODE)
			{
				return Settings.isShowAllApps();
			}
			return true;
		};
		this.applications = applications.stream().filter(VALID_APPS).collect(Collectors.toList());
	}

	public void init(int posX, int posY)
	{
		btnLeft = new Button(0, 0, Icons.CHEVRON_LEFT);
		btnLeft.setPadding(1);
		btnLeft.xPosition = posX + 3;
		btnLeft.yPosition = posY + 3;
		btnLeft.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(offset > 0)
            {
                offset--;
            }
        });
		btnRight = new Button(0, 0, Icons.CHEVRON_RIGHT);
		btnRight.setPadding(1);
		btnRight.xPosition = posX + 15 + 14 * APPS_DISPLAYED + 14;
		btnRight.yPosition = posY + 3;
		btnRight.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(offset + APPS_DISPLAYED < applications.size())
            {
                offset++;
            }
        });
		init();
	}

	public void onTick()
	{
		trayItems.forEach(TrayItem::tick);
	}
	
	public void render(Laptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY, float partialTicks)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(APP_BAR_GUI);
		gui.drawTexturedModalRect(x, y, 0, 0, 1, 18);
		int trayItemsWidth = trayItems.size() * 14;
		RenderUtil.drawRectWithTexture(x + 1, y, 1, 0, Laptop.SCREEN_WIDTH - 36 - trayItemsWidth, 18, 1, 18);
		RenderUtil.drawRectWithTexture(x + Laptop.SCREEN_WIDTH - 35 - trayItemsWidth, y, 2, 0, 35 + trayItemsWidth, 18, 1, 18);
		GlStateManager.disableBlend();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		btnLeft.render(gui, mc, btnLeft.xPosition, btnLeft.yPosition, mouseX, mouseY, true, partialTicks);
		//btnRight.render(gui, mc, btnRight.xPosition, btnLeft.yPosition, mouseX, mouseY, true, partialTicks);

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

		mc.fontRenderer.drawString(timeToString(mc.player.world.getWorldTime()), x + 334, y + 5, Color.WHITE.getRGB(), true);

		/* Settings App */
		int startX = x + 317;
		for(int i = 0; i < trayItems.size(); i++)
		{
			int posX = startX - (trayItems.size() - 1 - i) * 14;
			if(isMouseInside(mouseX, mouseY, posX, y + 2, posX + 13, y + 15))
			{
				Gui.drawRect(posX, y + 2, posX + 14, y + 16, new Color(1.0F, 1.0F, 1.0F, 0.1F).getRGB());
			}
			trayItems.get(i).getIcon().draw(mc, posX + 2, y + 4);
		}

		mc.getTextureManager().bindTexture(APP_BAR_GUI);

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

		int startX = x + 317;
		for(int i = 0; i < trayItems.size(); i++)
		{
			int posX = startX - (trayItems.size() - 1 - i) * 14;
			if(isMouseInside(mouseX, mouseY, posX, y + 2, posX + 13, y + 15))
			{
				trayItems.get(i).handleClick(mouseX, mouseY, mouseButton);
				break;
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
