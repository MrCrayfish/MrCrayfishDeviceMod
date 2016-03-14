package com.mrcrayfish.device.app;

import java.awt.Color;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.ButtonArrow;
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
		//btnWallpaperNext = new GuiButtonArrow(0, x + 40, y + 16, GuiButtonArrow.Type.RIGHT);
		//btnWallpaperPrev = new GuiButtonArrow(0, x + 5, y + 16, GuiButtonArrow.Type.LEFT);
		//TODO Add to component list
	}

	@Override
	public void render(Gui gui, Minecraft mc, int x, int y, int mouseX, int mouseY) 
	{
		gui.drawString(mc.fontRendererObj, "Wallpaper", x + 5, y + 5, Color.WHITE.getRGB());
		gui.drawCenteredString(mc.fontRendererObj, Integer.toString(GuiLaptop.currentWallpaper + 1), x + 28, y + 18, Color.WHITE.getRGB());
	}

	@Override
	public void handleButtonClick(Button button) 
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
	public void load(NBTTagCompound tagCompound) 
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) 
	{
		
	}

}
