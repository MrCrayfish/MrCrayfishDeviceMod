package com.mrcrayfish.device.app;

import java.awt.Color;
import java.util.List;

import com.mrcrayfish.device.app.components.Application;
import com.mrcrayfish.device.gui.GuiButtonArrow;
import com.mrcrayfish.device.gui.GuiLaptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationSettings extends Application
{
	private GuiButton btnWallpaperNext;
	private GuiButton btnWallpaperPrev;
	
	public ApplicationSettings() 
	{
		super("settings", "Settings", 80, 40);
	}

	@Override
	public void init(List<GuiButton> buttons, int x, int y) 
	{
		btnWallpaperNext = new GuiButtonArrow(0, x + 40, y + 16, GuiButtonArrow.Type.RIGHT);
		btnWallpaperPrev = new GuiButtonArrow(0, x + 5, y + 16, GuiButtonArrow.Type.LEFT);
		buttons.add(btnWallpaperNext);
		buttons.add(btnWallpaperPrev);
	}

	@Override
	public void render(Gui gui, Minecraft mc, int x, int y) 
	{
		gui.drawString(mc.fontRendererObj, "Wallpaper", x + 5, y + 5, Color.WHITE.getRGB());
		gui.drawCenteredString(mc.fontRendererObj, Integer.toString(GuiLaptop.currentWallpaper + 1), x + 28, y + 18, Color.WHITE.getRGB());
	}

	@Override
	public void handleButtonClick(GuiButton button) 
	{
		if(button == btnWallpaperNext)
		{
			GuiLaptop.nextWallpaper();
		}
		if(button == btnWallpaperPrev)
		{
			GuiLaptop.prevWallpaper();
		}
	}

	@Override
	public void updateButtons(int x, int y) 
	{
		btnWallpaperNext.xPosition = x + 40;
		btnWallpaperNext.yPosition = y + 16;
		btnWallpaperPrev.xPosition = x + 5;
		btnWallpaperPrev.yPosition = y + 16;
	}

	@Override
	public void hideButtons(List<GuiButton> buttons) 
	{
		buttons.remove(btnWallpaperNext);
		buttons.remove(btnWallpaperPrev);
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
