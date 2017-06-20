package com.mrcrayfish.device.programs.system;


import java.awt.*;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;

import com.mrcrayfish.device.programs.system.component.FileBrowser;
import com.mrcrayfish.device.programs.system.component.FileList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ApplicationFileBrowser extends Application
{
	private FileBrowser browser;

	public ApplicationFileBrowser() 
	{
		super("file_browser", "File Browser");
		this.setDefaultWidth(225);
		this.setDefaultHeight(145);
	}

	@Override
	public void init() 
	{
		super.init();
		browser = new FileBrowser(0, 0, this, false);
		this.addComponent(browser);
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
