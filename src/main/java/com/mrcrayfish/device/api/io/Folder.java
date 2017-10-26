package com.mrcrayfish.device.api.io;

import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.core.io.task.TaskGetFiles;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
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
	protected List<File> files = new ArrayList<>();

	private boolean synced = false;

	/**
	 * The default constructor for a folder
	 *
	 * @param name the name for the folder
	 */
	public Folder(String name)
	{
		this(name, false);
	}

	private Folder(String name, boolean protect)
	{
		this.name = name;
		this.protect = protect;
	}

	/**
	 * Adds a file to the folder. The folder must be in the file system before you can add files to
	 * it. If the file with the same name exists, it will not overridden. This method does not
	 * verify if the file was added successfully. See {@link #add(File, Callback)} to determine if
	 * it was successful or not.
	 *
	 * @param file the file to add
	 */
	public void add(File file)
	{
		add(file, false, null);
	}

	/**
	 * Adds a file to the folder. The folder must be in the file system before you can add files to
	 * it. If the file with the same name exists, it will not overridden. This method allows the
	 * specification of a {@link Callback}, and will return a
	 * {@link com.mrcrayfish.device.core.io.FileSystem.Response} indicating if the file was
	 * successfully added to the folder or an error occurred.
	 *
	 * @param file the file to add
	 * @param callback the response callback
	 */
	public void add(File file, @Nullable Callback<FileSystem.Response> callback)
	{
		add(file, false, callback);
	}

	/**
	 * Adds a file to the folder. The folder must be in the file system before you can add files to
	 * it. If the file with the same name exists, it can be overridden by passing true to the
	 * override parameter. This method also allows the specification of a {@link Callback}, and will
	 * return a {@link com.mrcrayfish.device.core.io.FileSystem.Response} indicating if the file was
	 * successfully added to the folder or an error occurred.
	 *
	 * @param file the file to add
	 * @param override if should override existing file
	 * @param callback the response callback
	 */
	public void add(File file, boolean override, @Nullable Callback<FileSystem.Response> callback)
	{
		if(!valid)
			throw new IllegalStateException("Folder must be added to the system before you can add files to it");

		if(file == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal file"), false);
			}
			return;
		}

		if(!FileSystem.PATTERN_FILE_NAME.matcher(file.name).matches())
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID_NAME, "Invalid file name"), true);
			}
			return;
		}

		if(hasFile(file.name))
		{
			if(!override)
			{
				if(callback != null)
				{
					callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "A file with that name already exists"), true);
				}
				return;
			}
			else if(getFile(file.name).isProtected())
			{
				if(callback != null)
				{
					callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Unable to override protected files"), true);
				}
				return;
			}
		}

		FileSystem.sendAction(drive, FileAction.Factory.makeNew(this, file, override), (response, success) ->
		{
            if(success)
			{
				file.setDrive(drive);
				file.valid = true;
				file.parent = this;
				files.add(file);
				FileBrowser.refreshList = true;
			}
			if(callback != null)
			{
				callback.execute(response, success);
			}
        });
	}

	/**
	 * Deletes the specified file name from the folder. The folder must be in the file system before
	 * you can delete files from it. If the file is not found, it will just fail silently. This
	 * method does not return a response if the file was deleted successfully. See
	 * {@link #delete(String, Callback)} (File, Callback)} to determine if it was successful or not.
	 *
	 * @param name the file name
	 */
	public void delete(String name)
	{
		delete(name, null);
	}

	/**
	 * Deletes the specified file name from the folder. The folder must be in the file system before
	 * you can delete files from it. This method also allows the specification of a {@link Callback}
	 * , and will return a {@link com.mrcrayfish.device.core.io.FileSystem.Response} indicating if
	 * the file was successfully deleted from the folder or an error occurred.
	 *
	 * @param name
	 * @param callback
	 */
	public void delete(String name, @Nullable Callback<FileSystem.Response> callback)
	{
		delete(getFile(name), callback);
	}

	/**
	 * Delete the specified file from the folder. The folder must be in the file system before
	 * you can delete files from it. If the file is not in this folder, it will just fail silently.
	 * This method does not return a response if the file was deleted successfully. See
	 * {@link #delete(String, Callback)} (File, Callback)} to determine if it was successful or not.
	 *
	 * @param file a file in this folder
	 */
	public void delete(File file)
	{
		delete(file, null);
	}

	/**
	 * Delete the specified file from the folder. The folder must be in the file system before
	 * you can delete files from it. The file must be in this folder, otherwise it will fail. This
	 * method also allows the specification of a {@link Callback}, and will return a
	 * {@link com.mrcrayfish.device.core.io.FileSystem.Response} indicating if the file was
	 * successfully deleted from the folder or an error occurred.
	 *
	 * @param file a file in this folder
	 * @param callback the response callback
	 */
	public void delete(File file, @Nullable Callback<FileSystem.Response> callback)
	{
		if(!valid)
			throw new IllegalStateException("Folder must be added to the system before you can delete files");

		if(file == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal file"), false);
			}
			return;
		}

		if(!files.contains(file))
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "The file does not exist in this folder"), false);
			}
			return;
		}

		if(file.isProtected())
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot delete protected files"), false);
			}
			return;
		}

		FileSystem.sendAction(drive, FileAction.Factory.makeDelete(file), (response, success) ->
        {
            if(success)
            {
                file.drive = null;
                file.valid = false;
                file.parent = null;
                files.remove(file);
                FileBrowser.refreshList = true;
            }
            if(callback != null)
            {
                callback.execute(response, success);
            }
        });
	}

	public void copyInto(File file, boolean override, boolean cut, @Nullable Callback<FileSystem.Response> callback)
	{
		if(file == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal file"), false);
			}
			return;
		}

		if(!file.valid || file.drive == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Source file is invalid"), false);
			}
			return;
		}

		if(hasFile(file.name))
		{
			if(!override)
			{
				if(callback != null)
				{
					callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "A file with that name already exists"), true);
				}
				return;
			}
			else if(getFile(file.name).isProtected())
			{
				if(callback != null)
				{
					callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Unable to override protected files"), true);
				}
				return;
			}
		}

		FileSystem.sendAction(file.drive, FileAction.Factory.makeCopyCut(file, this, false, cut), (response, success) ->
		{
			if(response.getStatus() == FileSystem.Status.SUCCESSFUL)
			{
				if(file.isFolder())
				{
					((Folder)file).copy();
				}
			}
		});
	}

	/**
	 * Checks if the folder contains a file for the specified name.
	 *
	 * @param name the name of the file to find
	 * @return if the file exists
	 */
	public boolean hasFile(String name)
	{
		return synced && files.stream().anyMatch(file -> file.name.equalsIgnoreCase(name));
	}

	/**
	 * Gets a file from this folder for the specified name. If the file is not found, it will return
	 * null.
	 *
	 * @param name the name of the file to get
	 * @return the found file
	 */
	@Nullable
	public File getFile(String name)
	{
		return files.stream().filter(file -> file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	/**
	 * Checks if the folder contains a folder for the specified name.
	 *
	 * @param name the name of the folder to find
	 * @return if the folder exists
	 */
	public boolean hasFolder(String name)
	{
		return synced && files.stream().anyMatch(file -> file.isFolder() && file.name.equalsIgnoreCase(name));
	}

	/**
	 * Gets a folder from this folder for the specified name. If the folder is not found, it will
	 * return null.
	 *
	 * @param name the name of the folder to get
	 * @return the found folder
	 */
	@Nullable
	public Folder getFolder(String name)
	{
		return (Folder) files.stream().filter(file -> file.isFolder() && file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public void getFolder(String name, Callback<Folder> callback)
	{
		Folder folder = getFolder(name);

		if(folder == null)
		{
			callback.execute(null, false);
			return;
		}

		if(!folder.isSynced())
		{
			Task task = new TaskGetFiles(folder, Laptop.getPos());
			task.setCallback((nbt, success) ->
			{
				if(success && nbt.hasKey("files", Constants.NBT.TAG_LIST))
				{
					NBTTagList files = nbt.getTagList("files", Constants.NBT.TAG_COMPOUND);
					folder.syncFiles(files);
					callback.execute(folder, true);
				}
				else
				{
					callback.execute(null, false);
				}
			});
			TaskManager.sendTask(task);
		}
		else
		{
			callback.execute(folder, true);
		}
	}

	/**
	 * Gets all the files in the folder.
	 *
	 * @return a list of files
	 */
	public List<File> getFiles()
	{
		return files;
	}

	/**
	 * Allows you to search this folder for files using a specified predicate. This only searches
	 * the folder itself and does not include any sub-folders. Once found, it will a list of all the
	 * files that matched the predicate.
	 *
	 * @param conditions the conditions of the file
	 * @return a list of found files
	 */
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

	/**
	 * Gets whether this file is actually folder
	 *
	 * @return is this file is a folder
	 */
	@Override
	public boolean isFolder()
	{
		return true;
	}

	/**
	 * Sets the data for this file. This does not work on folders and will fail silently.
	 * @param data the data to set
	 */
	@Override
	public void setData(@Nonnull NBTTagCompound data) {}

	/**
	 * Sets the data for this file. This does not work on folders and will fail silently. A callback
	 * can be specified but will be a guaranteed fail for folders.
	 *
	 * @param data the data to set
	 * @param callback the response callback
	 */
	@Override
	public void setData(@Nonnull NBTTagCompound data, Callback<FileSystem.Response> callback)
	{
		if(callback != null)
		{
			callback.execute(FileSystem.createResponse(FileSystem.Status.FAILED, "Can not set data of a folder"), false);
		}
	}

	@Override
	void setDrive(Drive drive)
	{
		this.drive = drive;
		files.forEach(f -> f.setDrive(drive));
	}

	/**
	 * Do not use! Syncs files from the file system
	 *
	 * @param tagList
	 */
	public void syncFiles(NBTTagList tagList)
	{
		files.removeIf(f -> !f.isFolder());
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound fileTag = tagList.getCompoundTagAt(i);
			File file = File.fromTag(fileTag.getString("file_name"), fileTag.getCompoundTag("data"));
			file.drive = drive;
			file.valid = true;
			file.parent = this;
			files.add(file);
		}
		synced = true;
	}

	/**
	 * Do not use! Used for checking if folder is synced with file system
	 *
	 * @return is folder synced
	 */
	public boolean isSynced()
	{
		return synced;
	}

	public void refresh()
	{
		synced = false;
	}

	/**
	 * Do not use! Used for validating files against file system
	 */
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

	/**
	 * Converts this folder into a tag compound. Due to how the file system works, this tag does not
	 * include the name of the folder and will have to be set manually for any storage.
	 *
	 * @return the folder tag
	 */
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

	/**
	 * Converts a tag compound to a folder instance.
	 *
	 * @param name the name of the folder
	 * @param folderTag the tag compound from {@link #toTag()}
	 * @return a folder instance
	 */
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
	 * Returns a copy of this folder. The copied folder is considered invalid and changes to it can
	 * not be made until it is added into the file system.
	 *
	 * @return copy of this folder
	 */
	@Override
	public File copy()
	{
		Folder folder = new Folder(name);
		files.forEach(f -> {
			File copy = f.copy();
			copy.protect = false;
			folder.files.add(copy);
		});
		return folder;
	}

	/**
	 * Returns a copy of this folder with a different name. The copied folder is considered invalid
	 * and changes to it can not be made until it is added into the file system.
	 *
	 * @param newName the new name for the folder
	 * @return copy of this folder
	 */
	@Override
	public File copy(String newName)
	{
		Folder folder = new Folder(newName);
		files.forEach(f -> {
			File copy = f.copy();
			copy.protect = false;
			folder.files.add(copy);
		});
		return folder;
	}
}
