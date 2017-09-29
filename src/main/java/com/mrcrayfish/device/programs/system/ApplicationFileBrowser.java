package com.mrcrayfish.device.programs.system;


import com.mrcrayfish.device.programs.system.component.FileBrowser;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationFileBrowser extends SystemApplication
{
	private FileBrowser browser;
	
	public ApplicationFileBrowser()
	{
		this.setDefaultWidth(225);
		this.setDefaultHeight(145);
	}

	@Override
	public void init() 
	{
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
