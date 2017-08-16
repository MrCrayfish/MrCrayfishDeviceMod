package com.mrcrayfish.device.api.io;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Folder extends File
{
	private List<File> files = new ArrayList<>();

	public Folder(String name)
	{
		this(name, false);
	}

	public Folder(String name, boolean protect)
	{
		this.name = name;
		this.protect = protect;
	}

	public boolean add(File file)
	{
		return add(file, false);
	}

	public boolean add(File file, boolean override)
	{
		if(file == null)
			throw new IllegalArgumentException("A null file can not be added to a folder");

		if(hasFile(file.name))
		{
			if(!override || getFile(file.name).isProtected())
				return false;
			files.remove(getFile(file.name));
		}

		files.add(file);
		file.parent = this;
		return true;
	}

	public boolean delete(String name)
	{
		return delete(getFile(name));
	}

	public boolean delete(File file)
	{
		if(file != null)
		{
			if(file.isProtected())
				return false;

			file.parent = null;
			files.remove(file);
			return true;
		}
		return false;
	}

	public boolean hasFile(String name)
	{
		return files.stream().anyMatch(file -> file.name.equalsIgnoreCase(name));
	}

	@Nullable
	public File getFile(String name)
	{
		return files.stream().filter(file -> file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public boolean hasFolder(String name)
	{
		return files.stream().anyMatch(file -> file.isFolder() && file.name.equalsIgnoreCase(name));
	}

	@Nullable
	public Folder getFolder(String name)
	{
		return (Folder) files.stream().filter(file -> file.isFolder() && file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public List<File> getFiles()
	{
		return files;
	}

	public List<File> search(Predicate<File> conditions, boolean includeSubFolders)
	{
		List<File> found = NonNullList.create();
		search(found, conditions, includeSubFolders);
		return found;
	}

	private void search(List<File> results, Predicate<File> conditions, boolean includeSubFolders)
	{
		files.stream().forEach(file ->
		{
			if(file.isFolder())
			{
				if(includeSubFolders)
				{
					((Folder) file).search(results, conditions, includeSubFolders);
				}
			}
			else if(conditions.test(file))
			{
				results.add(file);
			}
		});
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
		NBTTagCompound folderTag = new NBTTagCompound();

		NBTTagCompound fileList = new NBTTagCompound();
		files.stream().forEach(file -> fileList.setTag(file.getName(), file.toTag()));
		folderTag.setTag("files", fileList);

		if(protect) folderTag.setBoolean("protected", true);

		return folderTag;
	}

	public static Folder fromTag(String name, NBTTagCompound folderTag)
	{
		Folder folder = new Folder(name);

		if(folderTag.hasKey("protected", Constants.NBT.TAG_BYTE))
			folder.protect = folderTag.getBoolean("protected");

		NBTTagCompound fileList = folderTag.getCompoundTag("files");
		for(String fileName : fileList.getKeySet())
		{
			NBTTagCompound fileTag = fileList.getCompoundTag(fileName);
			if(fileTag.hasKey("files"))
			{
				folder.add(Folder.fromTag(fileName, fileTag));
			}
			else
			{
				folder.add(File.fromTag(fileName, fileTag));
			}
		}
		return folder;
	}

	@Override
	public void setData(@Nonnull NBTTagCompound data)
	{
		throw new DataException("Data can not be set to a folder");
	}

	@Override
	public File copy()
	{
		Folder folder = new Folder(name);
		files.forEach(f -> {
			File copy = f.copy();
			copy.protect = false;
			folder.add(copy);
		});
		return folder;
	}
}
