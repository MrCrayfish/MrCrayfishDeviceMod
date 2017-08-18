package com.mrcrayfish.device.programs.system;


import com.mrcrayfish.device.core.TaskBar;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ApplicationFileBrowser extends SystemApplication
{
	private FileBrowser browser;
	
		public ApplicationFileBrowser() 
	{
		super("file_browser", "File Browser");
		this.setDefaultWidth(225);
		this.setDefaultHeight(145);
		this.setIcon(TaskBar.APP_BAR_GUI, 0, 44);

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
