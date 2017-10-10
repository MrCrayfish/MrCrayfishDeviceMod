package com.mrcrayfish.device.api.io;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.regex.Pattern;

public class File
{
	public static final Pattern PATTERN_FILE_NAME = Pattern.compile("^[\\w. ]{1,32}$");

	public static final Comparator<File> SORT_BY_NAME = (f1, f2) -> {
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
		if(!PATTERN_FILE_NAME.matcher(name).matches())
			throw new IllegalArgumentException("Invalid file name. The name must match the regular expression: ^[\\w. ]{1,32}$");

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
	public void rename(@Nonnull String name)
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
	public void rename(@Nonnull String name, Callback<FileSystem.Response> callback)
	{
		if(!valid)
			throw new IllegalStateException("File must be added to the system before you can rename it");

		if(name.isEmpty())
		{
			if(callback != null)
			{
				callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_EMPTY_NAME, "Unable to rename file with that name"), true);
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
				callback.execute(response, false);
			}
        });
	}

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
	 *
	 * @return
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
	 *
	 * @return
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
	public void setData(@Nonnull NBTTagCompound data)
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
	public void setData(@Nonnull NBTTagCompound data, Callback callback)
	{
		if(!valid)
			throw new IllegalStateException("File must be added to the system before you can rename it");

		if(this.protect)
		{
			if(callback != null)
			{
				callback.execute(new NBTTagCompound(), false);
			}
			return;
		}

		FileSystem.sendAction(drive, FileAction.Factory.makeData(this, data), (nbt, success) ->
		{
			if(success)
			{
				this.data = data.copy();
			}
			if(callback != null)
			{
				callback.execute(nbt, success);
			}
        });
	}

	/**
	 *
	 * @return
	 */
	@Nullable
	public NBTTagCompound getData() 
	{
		return data.copy();
	}

	/**
	 *
	 * @return
	 */
	@Nullable
	public Folder getParent()
	{
		return parent;
	}

	public Drive getDrive()
	{
		return drive;
	}

	public void setDrive(Drive drive)
	{
		this.drive = drive;
	}

	/**
	 *
	 * @return
	 */
	public boolean isProtected()
	{
		return protect;
	}

	/**
	 *
	 * @return
	 */
	public boolean isFolder()
	{
		return false;
	}

	/**
	 *
	 * @param app
	 * @return
	 */
	public boolean isForApplication(Application app)
	{
		return openingApp != null && openingApp.equals(app.getInfo().getFormattedId());
	}

	/**
	 *
	 */
	public void delete()
	{
		delete(null);
	}

	/**
	 *
	 * @param callback
	 */
	public void delete(Callback callback)
	{
		if(!valid)
			throw new IllegalStateException("File must be added to the system before you can rename it");

		if(this.protect)
		{
			if(callback != null)
			{
				callback.execute(new NBTTagCompound(), false);
			}
			return;
		}

		if(parent != null)
		{
			parent.delete(this, callback);
		}
	}

	/**
	 *
	 * @return
	 */
	public NBTTagCompound toTag() 
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("openingApp", openingApp);
		tag.setTag("data", data);
		return tag;
	}

	/**
	 *
	 * @param name
	 * @param tag
	 * @return
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
	 *
	 * @return
	 */
	public File copy()
	{
		return new File(name, openingApp, data.copy());
	}

	public File copy(String newName)
	{
		return new File(newName, openingApp, data.copy());
	}
}
