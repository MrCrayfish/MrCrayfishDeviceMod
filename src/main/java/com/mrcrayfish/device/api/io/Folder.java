package com.mrcrayfish.device.api.io;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Folder extends File
{
	private List<File> files;
	
	public Folder(String name) 
	{
		super(name);
		this.files = new ArrayList<File>();
	}
	
	public boolean add(File file)
	{
		if(hasFile(file.name))
		{
			return false;
		}
		files.add(file);
		file.parent = this;
		return true;
	}
	
	public void delete(String name)
	{
		File file = getFile(name);
		if(file != null) 
		{
			file.parent = null;
			files.remove(file);
		}
	}
	
	public File getFile(String name)
	{
		for(File file : files)
		{
			if(file.name.equalsIgnoreCase(name))
			{
				return file;
			}
		}
		return null;
	}

	public boolean hasFile(String name)
	{
		for(File file : files) 
		{
			if(file.name.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}
	
	public List<File> getFiles()
	{
		return files;
	}
	
	@Override
	public boolean isFolder() 
	{
		return true;
	}
	
	@Override
	public NBTTagCompound toTag() 
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		NBTTagList list = new NBTTagList();
		for(File file : files) 
		{
			list.appendTag(file.toTag());
		}
		tag.setTag("files", list);
		return tag;
	}
	
	public static Folder fromTag(NBTTagCompound tag)
	{
		Folder folder = new Folder(tag.getString("name"));
		NBTTagList list = (NBTTagList) tag.getTag("files");
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound fileTag = list.getCompoundTagAt(i);
			File file = File.fromTag(fileTag);
			folder.files.add(file);
		}
		return folder;
	}

	@Override
	public void setData(NBTTagCompound data) 
	{
		throw new DataException("Data cannot be set to a folder");
	}
	
	@Override
	public NBTTagCompound getData() 
	{
		throw new DataException("Cannot retrieve data of a folder");
	}
	
	@Override
	public File copy() 
	{
		return new Folder(name);
	}
}
