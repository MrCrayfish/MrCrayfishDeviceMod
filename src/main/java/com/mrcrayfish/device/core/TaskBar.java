package com.mrcrayfish.device.core;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.SystemApplication;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
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
	}
	
	public void render(Laptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY, float partialTicks)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(APP_BAR_GUI);
		gui.drawTexturedModalRect(x, y, 0, 0, 1, 18);
		RenderUtil.drawRectWithTexture(x + 1, y, 1, 0, Laptop.SCREEN_WIDTH - 52, 18, 1, 18);
		RenderUtil.drawRectWithTexture(x + Laptop.SCREEN_WIDTH - 51, y, 2, 0, 51, 18, 1, 18);
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

		mc.fontRenderer.drawString(timeToString(mc.player.world.getWorldTime()), x + 334, y + 5, Color.WHITE.getRGB(), true);
		
		mc.getTextureManager().bindTexture(APP_BAR_GUI);

		if(isMouseInside(mouseX, mouseY, x + 317, y + 3, x + 328, y + 14))
		{
			Gui.drawRect(x + 316, y + 2, x + 330, y + 16, new Color(1.0F, 1.0F, 1.0F, 0.1F).getRGB());
		}

		/* Settings App */
		Icons.WIFI_NONE.draw(mc, x + 318, y + 4);

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

		if(isMouseInside(mouseX, mouseY, x + 317, y + 3, x + 328, y + 14))
		{
			if(Laptop.getSystem().hasContext())
			{
				Laptop.getSystem().closeContext();
			}
			else
			{
				Laptop.getSystem().openContext(createWifiMenu(), mouseX - 100, mouseY - 100);
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

	public static Layout createWifiMenu()
	{
		Layout layout = new Layout.Context(100, 100);
		layout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			Gui.drawRect(x, y, x + width, y + height, new Color(0.65F, 0.65F, 0.65F, 0.9F).getRGB());
		});

		ItemList<BlockPos> itemListRouters = new ItemList<>(5, 5, 90, 4);
		itemListRouters.setItems(getRouters());
		itemListRouters.setListItemRenderer(new ListItemRenderer<BlockPos>(16)
		{
			@Override
			public void render(BlockPos blockPos, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				Gui.drawRect(x, y, x + width, y + height, selected ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());
				gui.drawString(mc.fontRenderer, "Router", x + 16, y + 4, Color.WHITE.getRGB());

				BlockPos laptopPos = Laptop.getPos();
				double distance = Math.sqrt(blockPos.distanceSqToCenter(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5));
				if(distance > 20)
				{
					Icons.WIFI_LOW.draw(mc, x + 3, y + 3);
				}
				else if(distance > 10)
				{
					Icons.WIFI_MED.draw(mc, x + 3, y + 3);
				}
				else
				{
					Icons.WIFI_HIGH.draw(mc, x + 3, y + 3);
				}
			}
		});
		itemListRouters.sortBy((o1, o2) -> {
			BlockPos laptopPos = Laptop.getPos();
			double distance1 = Math.sqrt(o1.distanceSqToCenter(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5));
			double distance2 = Math.sqrt(o2.distanceSqToCenter(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5));
			return distance1 == distance2 ? 0 : distance1 > distance2 ? 1 : -1;
		});
		layout.addComponent(itemListRouters);

		Button buttonConnect = new Button(79, 79, Icons.CHECK);
		layout.addComponent(buttonConnect);

		return layout;
	}

	private static List<BlockPos> getRouters()
	{
		List<BlockPos> routers = new ArrayList<>();

		World world = Minecraft.getMinecraft().world;
		BlockPos laptopPos = Laptop.getPos();
		int range = 30;

		for(int y = -range; y < range + 1; y++)
		{
			for(int z = -range; z < range + 1; z++)
			{
				for(int x = -range; x < range + 1; x++)
				{
					BlockPos pos = new BlockPos(laptopPos.getX() + x, laptopPos.getY() + y, laptopPos.getZ() + z);
					IBlockState state = world.getBlockState(pos);
					if(state.getBlock() == DeviceBlocks.ROUTER)
					{
						routers.add(pos);
					}
				}
			}
		}
		return routers;
	}
}
