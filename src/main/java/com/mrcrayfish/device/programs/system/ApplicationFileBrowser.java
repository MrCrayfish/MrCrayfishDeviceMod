package com.mrcrayfish.device.programs.system;


import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
import net.minecraft.nbt.NBTTagCompound;

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
		browser = new FileBrowser(0, 0, this, getFileSystem().getRootFolder(), FileBrowser.Mode.FULL);
		this.addComponent(browser);

		browser.openFolder(getFileSystem().getHomeFolder(), true);
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
