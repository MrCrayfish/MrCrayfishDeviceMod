package com.mrcrayfish.device.core;

import com.mrcrayfish.device.api.io.Folder;

import net.minecraft.nbt.NBTTagCompound;

public class FileSystem 
{
	private Folder rootFolder;
	private Folder homeFolder;
	
	FileSystem(NBTTagCompound data) 
	{
		if(!data.hasNoTags())
		{
			rootFolder = Folder.fromTag(data);
		}
		else
		{
			setupDefaultSetup();
		}
	}

	private void setupDefaultSetup()
	{
		rootFolder = new Folder("Root");
		rootFolder.add(new Folder("Home"));
		rootFolder.add(new Folder("Application Data"));
	}

	public Folder getRootFolder()
	{
		return rootFolder;
	}
	
	public Folder getHomeFolder()
	{
		return (Folder) rootFolder.getFile("home");
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
