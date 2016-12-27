package com.mrcrayfish.device.api.app;

import net.minecraft.nbt.NBTTagCompound;

public abstract class Database {

	public abstract String getName();
	
	public abstract void save(NBTTagCompound tag);
	
	public abstract void load(NBTTagCompound tag);
}
