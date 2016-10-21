package com.mrcrayfish.device.programs;

import java.awt.Color;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.listener.ClickListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationTest extends Application
{
	public ApplicationTest()
	{
		super(Reference.MOD_ID + "TestApp", "Test App");
	}
	
	@Override
	public void init()
	{
		super.init();
		
		Layout one = new Layout(100, 100);
		one.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				gui.drawRect(x, y, x + width, y + height, Color.GREEN.getRGB());
			}
		});
		
		Label labelOne = new Label("Layout 1", 5, 5);
		one.addComponent(labelOne);
	
		this.addComponent(one);
		
		Layout two = new Layout(100, 0, 100, 100);
		two.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				gui.drawRect(x, y, x + width, y + height, Color.RED.getRGB());
			}
		});
		
		Label labelTwo = new Label("Layout 2", 5, 5);
		two.addComponent(labelTwo);
		
		this.addComponent(two);
		
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
