package com.mrcrayfish.device.app;

import java.awt.Color;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.ButtonArrow;
import com.mrcrayfish.device.app.listener.ClickListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationSettings extends Application
{
	private Button btnWallpaperNext;
	private Button btnWallpaperPrev;
	
	public ApplicationSettings() 
	{
		super("settings", "Settings");
		this.setDefaultWidth(80);
		this.setDefaultHeight(40);
	}

	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);
		btnWallpaperNext = new ButtonArrow(x, y, 40, 16, ButtonArrow.Type.RIGHT);
		btnWallpaperNext.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				Laptop.nextWallpaper();
			}
		});
		this.addComponent(btnWallpaperNext);
		
		btnWallpaperPrev = new ButtonArrow(x, y, 5, 16, ButtonArrow.Type.LEFT);
		btnWallpaperPrev.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				Laptop.prevWallpaper();
			}
		});
		this.addComponent(btnWallpaperPrev);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active) 
	{
		super.render(laptop, mc, x, y, mouseX, mouseY, active);
		laptop.drawString(mc.fontRendererObj, "Wallpaper", x + 5, y + 5, Color.WHITE.getRGB());
		laptop.drawCenteredString(mc.fontRendererObj, Integer.toString(Laptop.currentWallpaper + 1), x + 28, y + 18, Color.WHITE.getRGB());
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
