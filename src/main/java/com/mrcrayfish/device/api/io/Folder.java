package com.mrcrayfish.device.api.io;

import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.action.FileAction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

	private boolean synced = false;

	public Folder(String name)
	{
		this(name, false);
	}

	public Folder(String name, boolean protect)
	{
		if(!PATTERN_FILE_NAME.matcher(name).matches())
			throw new IllegalArgumentException("Invalid file name. The name must match the regular expression: ^[\\w. ]{1,32}$");

		this.name = name;
		this.protect = protect;
	}

	public void add(File file)
	{
		add(file, false, null);
	}

	public void add(@Nonnull File file, Callback<NBTTagCompound> callback)
	{
		add(file, false, callback);
	}

	public void add(@Nonnull File file, boolean override, Callback<NBTTagCompound> callback)
	{
		if(!valid)
			throw new IllegalStateException("Folder must be added to the system before you can add files to it");

		FileSystem.sendAction(drive, FileAction.Factory.makeNew(this, file, override), (nbt, success) ->
		{
            if(success)
			{
				file.drive = drive;
				file.valid = true;
				file.parent = this;
				files.add(file);
			}
			if(callback != null)
			{
				callback.execute(nbt, success);
			}
        });
	}

	public void delete(String name, Callback callback)
	{
		delete(getFile(name), callback);
	}

	public void delete(File file, Callback callback)
	{
		if(!valid)
			throw new IllegalStateException("Folder must be added to the system before you can delete files");

		if(file != null)
		{
			if(file.isProtected())
			{
				callback.execute(null, false);
			}

			FileSystem.sendAction(drive, FileAction.Factory.makeDelete(file), (nbt, success) ->
			{
				if(success)
				{
					file.drive = null;
					file.valid = false;
					file.parent = null;
					files.remove(file);
				}
				callback.execute(nbt, success);
			});
		}
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

	public List<File> search(Predicate<File> conditions)
	{
		List<File> found = NonNullList.create();
		search(found, conditions);
		return found;
	}

	private void search(List<File> results, Predicate<File> conditions)
	{
		files.stream().forEach(file ->
		{
			if(conditions.test(file))
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
				File file = Folder.fromTag(fileName, fileTag);
				file.parent = folder;
				folder.files.add(file);
			}
			else
			{
				File file = File.fromTag(fileName, fileTag);
				file.parent = folder;
				folder.files.add(file);
			}
		}
		return folder;
	}

	/**
	 * Do not use! This is strictly for internal use and will not reflect the actual file system.
	 * @param tagList
	 */
	public void syncFiles(NBTTagList tagList)
	{
		files.removeIf(f -> !f.isFolder());
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound fileTag = tagList.getCompoundTagAt(i);
			File file = File.fromTag(fileTag.getString("file_name"), fileTag.getCompoundTag("data"));
			file.valid = true;
			file.parent = this;
			files.add(file);
		}
		synced = true;
	}

	public boolean isSynced()
	{
		return synced;
	}

	public void validate()
	{
		if(!synced)
		{
			valid = true;
			files.forEach(f ->
			{
				if(f.isFolder())
				{
					((Folder)f).validate();
				}
				else
				{
					f.valid = true;
				}
			});
		}
	}

	@Override
	public void setData(@Nonnull NBTTagCompound data)
	{
		throw new DataException("Data can not be set to a folder");
	}

	@Override
	public void setDrive(Drive drive)
	{
		this.drive = drive;
		files.forEach(f -> f.setDrive(drive));
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

	@Override
	public File copy(String newName)
	{
		Folder folder = new Folder(newName);
		files.forEach(f -> {
			File copy = f.copy();
			copy.protect = false;
			folder.add(copy);
		});
		return folder;
	}
}
