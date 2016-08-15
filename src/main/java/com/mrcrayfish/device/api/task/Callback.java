package com.mrcrayfish.device.api.task;

import net.minecraft.nbt.NBTTagCompound;

public interface Callback {
	
	public void execute(NBTTagCompound nbt, boolean success);
}
