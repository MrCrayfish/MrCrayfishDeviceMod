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
		return add(file, false);
	}

	public boolean add(File file, boolean override)
	{
		if(file == null)
			throw new IllegalArgumentException("You cannot add a null file");

		if(hasFile(file.name))
		{
			if(!override) return false;
			files.remove(getFile(file.name));
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
		return files.stream().filter(file -> file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public boolean hasFile(String name)
	{
		return files.stream().anyMatch(file -> file.name.equalsIgnoreCase(name));
	}

	public boolean hasFolder(String name)
	{
		return files.stream().anyMatch(file -> file.isFolder() && file.name.equalsIgnoreCase(name));
	}
	
	public List<File> getFiles()
	{
		return files;
	}

	public void setFiles(List<File> files)
	{
		this.files = files;
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
		Folder folder = new Folder(name);
		files.forEach(f -> {
			folder.add(f.copy());
		});
		return folder;
	}
}
