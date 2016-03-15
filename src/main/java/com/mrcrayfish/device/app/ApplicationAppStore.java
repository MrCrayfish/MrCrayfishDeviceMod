package com.mrcrayfish.device.app;

import java.awt.Color;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.CheckBox;
import com.mrcrayfish.device.app.components.ItemList;
import com.mrcrayfish.device.app.components.Label;
import com.mrcrayfish.device.app.components.Slider;
import com.mrcrayfish.device.app.listener.SlideListener;
import com.mrcrayfish.device.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.object.AppInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationAppStore extends Application
{
	private Label label;
	private Slider slider;
	
	public ApplicationAppStore() 
	{
		super("app_store", "App Store", 250, 150);
	}

	@Override
	public void init(int x, int y)
	{
		super.init(x, y);
		
		label = new Label("", x, y, 5, 5);
		this.addComponent(label);
		
		slider = new Slider(x, y, 5, 20, 200);
		slider.setSlideListener(new SlideListener() {
			@Override
			public void onSlide(float percentage) {
				label.setText("Percentage: " + percentage);
			}
		});
		this.addComponent(slider);
		
		this.restoreDefaultLayout();
	}

	@Override
	public void render(Gui gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active) 
	{
		super.render(gui, mc, x, y, mouseX, mouseY, active);
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
