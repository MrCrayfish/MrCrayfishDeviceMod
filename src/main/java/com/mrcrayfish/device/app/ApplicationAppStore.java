package com.mrcrayfish.device.app;

import java.awt.Color;
import java.util.List;

import com.mrcrayfish.device.app.components.Application;
import com.mrcrayfish.device.app.components.ListItemRenderer;
import com.mrcrayfish.device.gui.GuiCheckBox;
import com.mrcrayfish.device.gui.GuiList;
import com.mrcrayfish.device.object.AppInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationAppStore extends Application
{
	private GuiList<AppInfo> list;
	private GuiCheckBox checkBox;
	
	public ApplicationAppStore() 
	{
		super("app_store", "App Store", 250, 150);
	}

	@Override
	public void init(List<GuiButton> buttons, int x, int y)
	{
		checkBox = new GuiCheckBox("Check Box", x + 130, y + 16);
		
		list = new GuiList<AppInfo>(x + 5, y + 16, 100, 4);
		list.init(buttons);
		list.addItem(new AppInfo("Hello"));
		list.addItem(new AppInfo("Cheese"));
		list.addItem(new AppInfo("Crackers"));
		list.addItem(new AppInfo("Hello"));
		list.addItem(new AppInfo("It's"));
		list.addItem(new AppInfo("Me"));
		
		ListItemRenderer<AppInfo> renderer = new ListItemRenderer<AppInfo>(20)
		{
			@Override
			public void render(AppInfo e, Gui gui, Minecraft mc, int x, int y, int width, boolean selected) 
			{
				if(selected)
					gui.drawRect(x, y, x + width, y + getHeight(), Color.DARK_GRAY.getRGB());
				else
					gui.drawRect(x, y, x + width, y + getHeight(), Color.GRAY.getRGB());
				gui.drawRect(x + 1, y + 1, x + 18, y + 18, Color.WHITE.getRGB());
				gui.drawString(mc.fontRendererObj, e.toString(), x + 20, y + 5, Color.WHITE.getRGB());
			}
		};
		
		list.setListItemRenderer(renderer);
	}

	@Override
	public void render(Gui gui, Minecraft mc, int x, int y) 
	{
		gui.drawString(mc.fontRendererObj, "Apps", x + 5, y + 5, Color.GREEN.getRGB());
		list.render(gui, mc);
		checkBox.render(mc);
	}
	
	@Override
	public void handleClick(Gui gui, int mouseX, int mouseY, int mouseButton) 
	{
		list.handleMouseClick(mouseX, mouseY, mouseButton);
		checkBox.onMouseClick(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleButtonClick(GuiButton button) 
	{
		list.handleButtonClick(button);
	}

	@Override
	public void updateButtons(int x, int y) 
	{
		list.xPosition = x + 5;
		list.yPosition = y + 16;
		checkBox.xPosition = x + 130;
		checkBox.yPosition = y + 16;
	}

	@Override
	public void hideButtons(List<GuiButton> buttons) 
	{
		list.handleClose(buttons);
	}

	@Override
	public void load(NBTTagCompound tagCompound) 
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) 
	{
		
	}
}
