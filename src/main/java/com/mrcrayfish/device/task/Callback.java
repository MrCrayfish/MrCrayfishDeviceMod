package com.mrcrayfish.device.task;

import net.minecraft.nbt.NBTTagCompound;

public interface Callback {
	
	public void execute(NBTTagCompound nbt, boolean success);
}
