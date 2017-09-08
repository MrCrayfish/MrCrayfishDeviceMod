package com.mrcrayfish.device.api.app;

import net.minecraft.nbt.NBTTagCompound;

public interface Database {

	String getName();
	
	void save(NBTTagCompound tag);
	
	void load(NBTTagCompound tag);
}
