package com.mrcrayfish.device.api.io;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.regex.Pattern;

public class File
{
	/**
	 * Comparator to sort the file list by alphabetical order. Folders are brought to the top.
	 */
	public static final Comparator<File> SORT_BY_NAME = (f1, f2) ->
	{
		if(f1.isFolder() && !f2.isFolder()) return -1;
		if(!f1.isFolder() && f2.isFolder()) return 1;
		return f1.name.compareTo(f2.name);
	};

	protected Drive drive;
	protected Folder parent;
	protected String name;
	protected String openingApp;
	protected NBTTagCompound data;
	protected boolean protect = false;
	protected boolean valid = false;

	protected File() {}

	/**
	 * The standard constructor for a file
	 *
	 * @param name
	 * @param app
	 * @param data
	 */
	public File(String name, Application app, NBTTagCompound data) 
	{
		this(name, app.getInfo().getFormattedId(), data, false);
	}

	/**
	 * The alternate constructor for a file. This second constructor allows the specification of
	 * an application identifier. This allows the creation of files for different applications. You
	 * should know the format of the target file if you are using this constructor
	 *
	 * @param name
	 * @param openingAppId
	 * @param data
	 */
	public File(String name, String openingAppId, NBTTagCompound data)
	{
		this(name, openingAppId, data, false);
	}

	private File(String name, String openingAppId, NBTTagCompound data, boolean protect)
	{
		this.name = name;
		this.openingApp = openingAppId;
		this.data = data;
		this.protect = protect;
	}

	/**
	 * Gets the name of the file
	 *
	 * @return the file name
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Renames the file with the specified name. This method is asynchronous, so the name will not
	 * be set immediately. It will ignore if the rename failed. Use
	 * {@link File#rename(String,Callback)} instead if you need to know it that it successfully
	 * renamed the file.
	 *
	 * @param name the new file name
	 */
	public void rename(String name)
	{
		rename(name, null);
	}

	/**
	 * Renames the file with the specified name and allows a callback to be specified. This method
	 * is asynchronous, so the name will not be set immediately. The callback is fired when the file
	 * has been renamed, however it is not necessarily successful.
	 *
	 * @param name the new file name
	 */
	public void rename(String name, @Nullable Callback<FileSystem.Response> callback)
	{
		if(!valid)
			throw new IllegalStateException("File must be added to the system before you can rename it");

		if(protect)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot rename a protected file"), false);
			}
			return;
		}

		if(!FileSystem.PATTERN_FILE_NAME.matcher(name).matches())
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID_NAME, "Invalid file name"), true);
			}
			return;
		}

		FileSystem.sendAction(drive, FileAction.Factory.makeRename(this, name), (response, success) ->
		{
			if(success)
			{
				this.name = name;
			}
			if(callback != null)
			{
				callback.execute(response, success);
			}
        });
	}

	/**
	 * Gets the path of this file. The path is the set of all the folders needed to traverse in
	 * order to find the folder this file is contained within and appends the file's name at the
	 * end. This is different to {@link #getLocation()} which does not append the file's name.
	 *
	 * @return the path of the file
	 */
	public String getPath()
	{
		if(parent == null)
			return "/";

		StringBuilder builder = new StringBuilder();

		File current = this;
		while(current != null)
		{
			if(current.getParent() == null) break;
			builder.insert(0, "/" + current.getName());
			current = current.getParent();
		}
		return builder.toString();
	}

	/**
	 * Gets the location of this file. The location is the set of folders needed to traverse in
	 * order to find the folder this file is contained within. This is different to
	 * {@link #getPath()} and does not include the file name on the end.
	 *
	 * @return the location of the file
	 */
	public String getLocation()
	{
		if(parent == null)
			throw new NullPointerException("File must have a parent to compile the directory");

		StringBuilder builder = new StringBuilder();

		Folder current = parent;
		while(current != null)
		{
			if(current.getParent() == null) break;
			builder.insert(0, "/" + current.getName());
			current = current.getParent();
		}
		return builder.toString();
	}

	/**
	 * Gets the application this file can be open with.
	 *
	 * @return the application identifier
	 */
	@Nullable
	public String getOpeningApp() 
	{
		return openingApp;
	}

	/**
	 * Sets the data for the file. This method is asynchronous, so data will not be set immediately.
	 *
	 * @param data
	 */
	public void setData(NBTTagCompound data)
	{
		setData(data, null);
	}

	/**
	 * Sets the data for the file and allows a callback to be specified. This method is
	 * asynchronous, so data will not be set immediately. The callback is fired when the data is
	 * set, however it is not necessarily successful.
	 *
	 * @param data
	 * @param callback
	 */
	public void setData(NBTTagCompound data, @Nullable Callback<FileSystem.Response> callback)
	{
		if(!valid)
			throw new IllegalStateException("File must be added to the system before you can rename it");

		if(protect)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot set data on a protected file"), false);
			}
			return;
		}

		if(data == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID_DATA, "Invalid data"), false);
			}
			return;
		}

		FileSystem.sendAction(drive, FileAction.Factory.makeData(this, data), (response, success) ->
		{
			if(success)
			{
				this.data = data.copy();
			}
			if(callback != null)
			{
				callback.execute(response, success);
			}
        });
	}

	/**
	 * Gets the data of this file. The data you receive is a copied version. If you want to update
	 * it, use {@link #setData(NBTTagCompound, Callback)} to do so.
	 *
	 * @return the file's data
	 */
	@Nullable
	public NBTTagCompound getData() 
	{
		return data.copy();
	}

	/**
	 * Gets the {@link Folder} this file is contained in.
	 *
	 * @return the parent of this file
	 */
	@Nullable
	public Folder getParent()
	{
		return parent;
	}

	/**
	 * Gets the drive this file belongs to.
	 *
	 * @return the drive this file is contained in
	 */
	public Drive getDrive()
	{
		return drive;
	}

	/**
	 * Sets the drive for this file.
	 *
	 * @param drive the drive this file is contained in
	 */
	void setDrive(Drive drive)
	{
		this.drive = drive;
	}

	/**
	 * Gets the protected flag of this file.
	 *
	 * @return the protected flag
	 */
	public boolean isProtected()
	{
		return protect;
	}

	/**
	 * Gets whether this file is actually folder
	 *
	 * @return is this file is a folder
	 */
	public boolean isFolder()
	{
		return false;
	}

	/**
	 * Determines if this file is for the specified application. This helps identify files that are
	 * designed for the specified application. Useful in filtering out files in a list.
	 *
	 * @param app the application to test against
	 *
	 * @return if this file is for the application
	 */
	public boolean isForApplication(Application app)
	{
		return openingApp != null && openingApp.equals(app.getInfo().getFormattedId());
	}

	/**
	 * Deletes this file from the folder its contained in. This method does not specify a callback,
	 * so any errors occurred will not be reported.
	 */
	public void delete()
	{
		delete(null);
	}

	/**
	 * Deletes this file from the folder its contained in. This method allows the specification of a
	 * callback and will tell if deleted successfully or not.
	 *
	 * @param callback the callback
	 */
	public void delete(@Nullable Callback<FileSystem.Response> callback)
	{
		if(!valid)
			throw new IllegalStateException("File must be added to the system before you can rename it");

		if(this.protect)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot delete a protected file"), false);
			}
			return;
		}

		if(parent != null)
		{
			parent.delete(this, callback);
		}
	}

	public void copyTo(Folder destination, boolean override, @Nullable Callback<FileSystem.Response> callback)
	{
		if(destination == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal folder"), false);
			}
			return;
		}

		if(!destination.valid || destination.drive == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Destination folder is invalid"), false);
			}
			return;
		}

		if(!valid || drive == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Source file is invalid"), false);
			}
			return;
		}

		if(destination.hasFile(name))
		{
			if(!override)
			{
				if(callback != null)
				{
					callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "A file with that name already exists"), false);
				}
				return;
			}
			else if(destination.getFile(name).isProtected())
			{
				if(callback != null)
				{
					callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Unable to override protected files"), false);
				}
				return;
			}
		}

		FileSystem.sendAction(drive, FileAction.Factory.makeCopyCut(this, destination, override, false), (response, success) ->
		{
			if(response.getStatus() == FileSystem.Status.SUCCESSFUL)
			{
				if(override)
				{
					destination.files.remove(destination.getFile(name));
				}
				File file = copy();
				file.valid = true;
				file.parent = destination;
				file.setDrive(destination.drive);
				destination.files.add(file);
				FileBrowser.refreshList = true;
			}
			if(callback != null)
			{
				callback.execute(response, success);
			}
		});
	}

	public void moveTo(Folder destination, boolean override, @Nullable Callback<FileSystem.Response> callback)
	{
		if(destination == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal folder"), false);
			}
			return;
		}

		if(!destination.valid || destination.drive == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Destination folder is invalid"), false);
			}
			return;
		}

		if(!valid || drive == null)
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Source file is invalid"), false);
			}
			return;
		}

		if(destination.hasFile(name))
		{
			if(!override)
			{
				if(callback != null)
				{
					callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "A file with that name already exists"), false);
				}
				return;
			}
			else if(destination.getFile(name).isProtected())
			{
				if(callback != null)
				{
					callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Unable to override protected files"), false);
				}
				return;
			}
		}

		FileSystem.sendAction(drive, FileAction.Factory.makeCopyCut(this, destination, override, true), (response, success) ->
		{
			if(response.getStatus() == FileSystem.Status.SUCCESSFUL)
			{
				if(override)
				{
					destination.files.remove(destination.getFile(name));
				}
				parent.files.remove(this);
				setDrive(destination.drive);
				parent = destination;
				destination.files.add(this);
				FileBrowser.refreshList = true;
			}
			if(callback != null)
			{
				callback.execute(response, success);
			}
		});
	}

	/**
	 * Converts this file into a tag compound. Due to how the file system works, this tag does not
	 * include the name of the file and will have to be set manually for any storage.
	 *
	 * @return the file tag
	 */
	public NBTTagCompound toTag() 
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("openingApp", openingApp);
		tag.setTag("data", data);
		return tag;
	}

	/**
	 * Converts a tag compound to a file instance.
	 *
	 * @param name the name of the file
	 * @param tag the tag compound from {@link #toTag()}
	 * @return a file instance
	 */
	public static File fromTag(String name, NBTTagCompound tag)
	{
		return new File(name, tag.getString("openingApp"), tag.getCompoundTag("data"));
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		if(!(obj instanceof File))
			return false;
		File file = (File) obj;
		return parent == file.parent && name.equalsIgnoreCase(file.name);
	}

	/**
	 * Returns a copy of this file. The copied file is considered invalid and changes to it can not
	 * be made until it is added into the file system.
	 *
	 * @return copy of this file
	 */
	public File copy()
	{
		return new File(name, openingApp, data.copy());
	}

	/**
	 * Returns a copy of this file with a different name. The copied file is considered invalid and
	 * changes to it can not be made until it is added into the file system.
	 *
	 * @param newName the new name for the file
	 * @return copy of this file
	 */
	public File copy(String newName)
	{
		return new File(newName, openingApp, data.copy());
	}
}
