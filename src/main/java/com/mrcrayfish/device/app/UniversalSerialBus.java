package com.mrcrayfish.device.app;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class UniversalSerialBus 
{
	private ItemStack item;
	
	public UniversalSerialBus(ItemStack item)
	{
		this.item = item;
	}
	
	public void writeData(String id, NBTTagCompound data) throws Exception
	{
		if(!item.hasTagCompound())
		{
			item.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound tagCompound = item.getTagCompound();
		if(!tagCompound.hasKey(id))
		{
			tagCompound.setTag(id, data);
		}
		else
		{
			throw new Exception("Data for that ID already exists. Remove it or change your ID.");
		}
	}
	
	public NBTTagCompound readData(String id)
	{
		if(item.hasTagCompound())
		{
			return item.getTagCompound().getCompoundTag(id);
		}
		return new NBTTagCompound();
	}
	
	public void removeData(String id)
	{
		if(item.hasTagCompound())
		{
			item.getTagCompound().removeTag(id);
		}
	}
}
