package com.mrcrayfish.device.object;

import net.minecraft.nbt.NBTTagCompound;

public class Note {

	public String title;
	public String content;
	
	public Note(String title, String content) {
		this.title = title;
		this.content = content;
	}
	
	@Override
	public String toString() 
	{
		return title;
	}
	
	public static Note readFromNBT(NBTTagCompound tagCompound)
	{
		return new Note(tagCompound.getString("Title"), tagCompound.getString("Content"));		
	}
}
