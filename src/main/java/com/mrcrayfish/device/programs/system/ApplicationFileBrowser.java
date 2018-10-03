package com.mrcrayfish.device.programs.system;

import javax.annotation.Nullable;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.object.TrayItem;
import com.mrcrayfish.device.programs.system.component.FileBrowser;

import net.minecraft.nbt.NBTTagCompound;

public class ApplicationFileBrowser extends SystemApplication
{
	private FileBrowser browser;

	public ApplicationFileBrowser()
	{
		this.setDefaultWidth(211);
		this.setDefaultHeight(145);
	}

	@Override
	public void init(@Nullable NBTTagCompound intent)
	{
		this.defaultLayout = new Layout();
		
		browser = new FileBrowser(0, 0, this, FileBrowser.Mode.FULL);
		browser.openFolder(FileSystem.DIR_HOME);
		this.addComponent(browser);

		this.setMinimumSize(this.getDefaultLayout().width, this.getDefaultLayout().height);
		this.setResizable(true);
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{
	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{
	}

	@Override
	public void onResize(int width, int height)
	{
		super.onResize(width, height);
		browser.resize(width, height);
	}

	public static class FileBrowserTrayItem extends TrayItem
	{
		public FileBrowserTrayItem()
		{
			super(Icons.FOLDER);
		}

		@Override
		public void handleClick(int mouseX, int mouseY, int mouseButton)
		{
			AppInfo info = ApplicationManager.getApplication("cdm:file_browser");
			if (info != null)
			{
				Laptop.getSystem().openApplication(info);
			}
		}
	}
}
