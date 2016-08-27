package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ButtonArrow;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.TaskBar;

import net.minecraft.nbt.NBTTagCompound;

public class ApplicationSettings extends Application
{
	private Button btnWallpaperNext;
	private Button btnWallpaperPrev;
	
	public ApplicationSettings() 
	{
		super("settings", "Settings", TaskBar.APP_BAR_GUI, 14, 30);
		this.setDefaultWidth(80);
		this.setDefaultHeight(40);
	}

	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);
		btnWallpaperNext = new ButtonArrow(40, 16, ButtonArrow.Type.RIGHT);
		btnWallpaperNext.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				Laptop.nextWallpaper();
			}
		});
		super.addComponent(btnWallpaperNext);
		
		btnWallpaperPrev = new ButtonArrow(5, 16, ButtonArrow.Type.LEFT);
		btnWallpaperPrev.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				Laptop.prevWallpaper();
			}
		});
		super.addComponent(btnWallpaperPrev);
	}

	/*@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active) 
	{
		super.render(laptop, mc, mouseX, mouseY, active);
		laptop.drawString(mc.fontRendererObj, "Wallpaper", x + 5, y + 5, Color.WHITE.getRGB());
		laptop.drawCenteredString(mc.fontRendererObj, Integer.toString(Laptop.currentWallpaper + 1), x + 28, y + 18, Color.WHITE.getRGB());
	}*/

	@Override
	public void load(NBTTagCompound tagCompound) 
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) 
	{
		
	}

}
