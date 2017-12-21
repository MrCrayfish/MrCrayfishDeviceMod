package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.programs.system.component.FileBrowser;

import net.minecraft.nbt.NBTTagCompound;

public class ApplicationFileBrowser extends SystemApplication {
	private FileBrowser browser;

	public ApplicationFileBrowser() {
		setDefaultWidth(225);
		setDefaultHeight(145);
	}

	@Override
	public void init() {
		browser = new FileBrowser(0, 0, this, FileBrowser.Mode.FULL);
		browser.openFolder(FileSystem.DIR_HOME);
		addComponent(browser);
	}

	@Override
	public void load(NBTTagCompound tagCompound) {

	}

	@Override
	public void save(NBTTagCompound tagCompound) {

	}

}
