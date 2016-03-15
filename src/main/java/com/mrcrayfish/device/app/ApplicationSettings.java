package com.mrcrayfish.device.app;

import java.awt.Color;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.ButtonArrow;
import com.mrcrayfish.device.app.listener.ClickListener;
import com.mrcrayfish.device.gui.GuiLaptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationSettings extends Application
{
	private Button btnWallpaperNext;
	private Button btnWallpaperPrev;
	
	public ApplicationSettings() 
	{
		super("settings", "Settings", 80, 40);
	}

	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);
		btnWallpaperNext = new ButtonArrow(x, y, 40, 16, ButtonArrow.Type.RIGHT);
		btnWallpaperNext.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				GuiLaptop.nextWallpaper();
			}
		});
		this.addComponent(btnWallpaperNext);
		
		btnWallpaperPrev = new ButtonArrow(x, y, 5, 16, ButtonArrow.Type.LEFT);
		btnWallpaperPrev.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				GuiLaptop.prevWallpaper();
			}
		});
		this.addComponent(btnWallpaperPrev);
	}

	@Override
	public void render(Gui gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active) 
	{
		super.render(gui, mc, x, y, mouseX, mouseY, active);
		gui.drawString(mc.fontRendererObj, "Wallpaper", x + 5, y + 5, Color.WHITE.getRGB());
		gui.drawCenteredString(mc.fontRendererObj, Integer.toString(GuiLaptop.currentWallpaper + 1), x + 28, y + 18, Color.WHITE.getRGB());
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
