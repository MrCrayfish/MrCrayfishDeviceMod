package com.mrcrayfish.device.core;

import com.mrcrayfish.device.api.io.Folder;
import net.minecraft.nbt.NBTTagCompound;

public class FileSystem 
{
	private Folder rootFolder;
	
	FileSystem(NBTTagCompound data) 
	{
		if(!data.hasNoTags())
		{
			rootFolder = Folder.fromTag(data);
		}
		setupDefaultSetup();
	}

	private void setupDefaultSetup()
	{
		if(rootFolder == null)
		{
			rootFolder = new Folder("Root");
		}
		if(!rootFolder.hasFolder("Home"))
		{
			rootFolder.add(new Folder("Home"), true);
		}
		if(!rootFolder.hasFolder("Application Data"))
		{
			rootFolder.add(new Folder("Application Data"), true);
		}
	}

	public Folder getRootFolder()
	{
		return rootFolder;
	}
	
	public Folder getHomeFolder()
	{
		return (Folder) rootFolder.getFile("Home");
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
