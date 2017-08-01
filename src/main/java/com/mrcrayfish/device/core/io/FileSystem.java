package com.mrcrayfish.device.core.io;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileSystem 
{
	private static boolean dirty = false;

	private List<Action> actionList = new ArrayList<>();
	private Folder rootFolder;
	
	public FileSystem(NBTTagCompound data)
	{
		if(!data.hasNoTags() && data.hasKey("Root"))
		{
			rootFolder = Folder.fromTag("Root", data.getCompoundTag("Root"));
		}
		setupDefaultSetup();
	}

	/**
	 * Sets up the default folders for the file system.
	 */
	private void setupDefaultSetup()
	{
		if(rootFolder == null)
		{
			rootFolder = new Folder("Root");
		}
		if(!rootFolder.hasFolder("Home"))
		{
			rootFolder.add(new Folder("Home"), true);
		}
		if(!rootFolder.hasFolder("Application Data"))
		{
			rootFolder.add(new Folder("Application Data"), true);
		}
	}

	public void updateData(NBTTagCompound tag)
	{
		rootFolder = Folder.fromTag("Root", tag.getCompoundTag("Root"));
	}

	private static final Pattern PATTERN_DIRECTORY = Pattern.compile("^[a-zA-Z0-9 ]{1,16}(/[a-zA-Z0-9 ]{1,16})*/?$");

	/**
	 * Gets a folder in the file system. To get sub folders, simply use a
	 * '/' between each folder name. If the folder does not exist, it will
	 * return null.
	 *
	 * @param path the directory of the folder
	 */
	@Nullable
	public Folder getFolder(@Nonnull String path)
	{
		if(path == null)
			throw new NullPointerException("The path can not be null");

		if(!PATTERN_DIRECTORY.matcher(path).matches())
			throw new IllegalArgumentException("The path \"" + path + "\" does not follow the correct format");

		Folder prev = rootFolder;
		String[] folders = path.split("/");
		if(folders.length > 0 && folders.length <= 10)
		{
			for(int i = 1; i < folders.length; i++)
			{
				Folder temp = prev.getFolder(folders[i]);
				if(temp == null) return null;
				prev = temp;
			}
			return prev;
		}
		return null;
	}

	/**
	 * Gets the root folder of the file system
	 *
	 * @return the root folder
	 */
	public Folder getRootFolder()
	{
		return rootFolder;
	}

	/**
	 * Gets the root folder of the file system
	 *
	 * @return the root folder
	 */
	public Folder getHomeFolder()
	{
		return rootFolder.getFolder("Home");
	}

	/**
	 *
	 * @return
	 */
	public boolean isUSBPluggedIn()
	{
		//TODO Usb
		return false;
	}
	
	public Folder getUSB()
	{
		//TODO Usb
		return null;
	}

	public static void sendAction(BlockPos laptopPosition, Action action)
	{
		//TaskPipeline.sendTask(new TaskSendAction(laptopPosition, action));
	}

	public void readAction(Action action)
	{
		Folder folder = getFolder(action.data.getString("directory"));
		NBTTagCompound data = action.data.getCompoundTag("data");
		switch(action.type)
		{
			case NEW:
				folder.add(new File(action.data.getString("file_name"), data.getString("openingApp"), data.getCompoundTag("data")));
				break;
			case DELETE:
				folder.delete(action.data.getString("file_name"));
				break;
			case RENAME:
				File file = folder.getFile(action.data.getString("file_name"));
				if(file != null) file.rename(action.data.getString("new_file_name"));
				break;
		}
	}

	public NBTTagCompound toTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("Root", rootFolder.toTag());
		return tag;
	}

	public static FileSystem fromTag(NBTTagCompound tag)
	{
		return tag == null ? new FileSystem(new NBTTagCompound()) : new FileSystem(tag);
	}

	public static class Action
	{
		private Type type;
		private NBTTagCompound data;

		private Action(Type type, NBTTagCompound data)
		{
			this.type = type;
			this.data = data;
		}

		public NBTTagCompound toTag()
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", type.ordinal());
			tag.setTag("data", data);
			return tag;
		}

		public static Action fromTag(NBTTagCompound tag)
		{
			Type type = Type.values()[tag.getInteger("type")];
			NBTTagCompound data = tag.getCompoundTag("data");
			return new Action(type, data);
		}

		public enum Type
		{
			NEW, DELETE, RENAME;
		}
	}

	public static class ActionFactory
	{
		public static Action makeNewAction(File file)
		{
			NBTTagCompound data = new NBTTagCompound();
			data.setString("directory", compileDirectory(file));
			data.setString("file_name", file.getName());
			data.setTag("data", file.toTag());
			return new Action(Action.Type.NEW, data);
		}

		public static Action makeDeleteAction(File file)
		{
			NBTTagCompound data = new NBTTagCompound();
			data.setString("directory", compileDirectory(file));
			data.setString("file_name", file.getName());
			return new Action(Action.Type.DELETE, data);
		}

		public static Action makeRenameAction(File file, String newFileName)
		{
			NBTTagCompound data = new NBTTagCompound();
			data.setString("directory", compileDirectory(file));
			data.setString("file_name", file.getName());
			data.setString("new_file_name", newFileName);
			return new Action(Action.Type.RENAME, data);
		}

		private static String compileDirectory(File file)
		{
			if(file.getParent() == null)
				throw new NullPointerException("File must have a parent to compile the directory");

			StringBuilder builder = new StringBuilder();

			Folder parent = file.getParent();
			while(parent != null)
			{
				builder.insert(0, "/").insert(0, parent.getName());
				parent = parent.getParent();
			}
			return builder.subSequence(1, builder.length()).toString();
		}
	}
}
