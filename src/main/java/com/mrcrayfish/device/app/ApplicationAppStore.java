package com.mrcrayfish.device.app;

import java.awt.Color;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.CheckBox;
import com.mrcrayfish.device.app.components.ItemList;
import com.mrcrayfish.device.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.object.AppInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationAppStore extends Application
{
	private Layout main;
	private ItemList<AppInfo> list;
	private CheckBox checkBox;
	
	public ApplicationAppStore() 
	{
		super("app_store", "App Store", 250, 150);
	}

	@Override
	public void init(int x, int y)
	{
		checkBox = new CheckBox("Check Box", x, y, 130, 16);
		this.addComponent(checkBox);
		
		list = new ItemList<AppInfo>(x, y, 5, 16, 100, 4);
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
		this.addComponent(list);
	}

	@Override
	public void render(Gui gui, Minecraft mc, int x, int y, int mouseX, int mouseY) 
	{
		super.render(gui, mc, x, y, mouseX, mouseY);
		gui.drawString(mc.fontRendererObj, "Apps", x + 5, y + 5, Color.GREEN.getRGB());
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
