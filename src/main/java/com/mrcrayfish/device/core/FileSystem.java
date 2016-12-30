package com.mrcrayfish.device.core;

import com.mrcrayfish.device.api.io.Folder;

import net.minecraft.nbt.NBTTagCompound;

public class FileSystem 
{
	private Folder homeFolder;
	
	FileSystem(NBTTagCompound data) 
	{
		homeFolder = new Folder("home");
		if(!data.hasNoTags())
		{
			homeFolder = Folder.fromTag(data);
		}
	}
	
	public Folder getBaseFolder()
	{
		return homeFolder;
	}
	
	public boolean isUSBPluggedIn()
	{
		//TODO Usb
		return false;
	}
	
	public void getUSB()
	{
		//TODO Usb
	}
}
