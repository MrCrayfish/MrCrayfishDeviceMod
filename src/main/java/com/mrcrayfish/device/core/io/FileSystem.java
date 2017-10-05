package com.mrcrayfish.device.core.io;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileSystem
{
	public static final String DIR_ROOT = "/";
	public static final String DIR_APPLICATION_DATA = DIR_ROOT + "Application Data";
	public static final String DIR_HOME = DIR_ROOT + "Home";

	private static boolean dirty = false;

	private List<FileAction> actionList = new ArrayList<>();
	private ServerFolder rootFolder;
	
	private FileSystem(NBTTagCompound data)
	{
		if(!data.hasNoTags() && data.hasKey("Root"))
		{
			rootFolder = ServerFolder.fromTag("Root", data.getCompoundTag("Root"));
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
			rootFolder = createProtectedFolder("Root");
		}
		if(!rootFolder.hasFolder("Home"))
		{
			rootFolder.add(createProtectedFolder("Home"), false);
		}
		if(!rootFolder.hasFolder("Application Data"))
		{
			rootFolder.add(createProtectedFolder("Application Data"), false);
		}
	}

	private ServerFolder createProtectedFolder(String name)
	{
		try
		{
			Constructor<ServerFolder> constructor = ServerFolder.class.getDeclaredConstructor(String.class, boolean.class);
			constructor.setAccessible(true);
			return constructor.newInstance(name, true);
		}
		catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}


	public void updateData(NBTTagCompound tag)
	{
		rootFolder = ServerFolder.fromTag("Root", tag.getCompoundTag("Root"));
	}

	public static final Pattern PATTERN_DIRECTORY = Pattern.compile("^(/)|(/[a-zA-Z0-9 ]{1,32})*$");

	/**
	 * Gets a folder in the file system. To get sub folders, simply use a
	 * '/' between each folder name. If the folder does not exist, it will
	 * return null.
	 *
	 * @param path the directory of the folder
	 */
	@Nullable
	public ServerFolder getFolder(@Nonnull String path, boolean create)
	{
		if(path == null)
			throw new NullPointerException("The path can not be null");

		if(!PATTERN_DIRECTORY.matcher(path).matches())
			throw new IllegalArgumentException("The path \"" + path + "\" does not follow the correct format");

		if(path.equals("/")) return rootFolder;

		ServerFolder prev = rootFolder;
		String[] folders = path.split("/");
		if(folders.length > 0 && folders.length <= 10)
		{
			for(int i = 1; i < folders.length; i++)
			{
				ServerFolder temp = prev.getFolder(folders[i]);
				if(temp == null)
				{
					if(!create) return null;
					ServerFolder newFolder = new ServerFolder(folders[i], false);
					temp.add(newFolder, false);
					temp = newFolder;
				}

				prev = temp;
			}
			return prev;
		}
		return null;
	}

	public ServerFolder getFolderStructure()
	{
		return rootFolder.copyStructure();
	}

	public static void sendAction(FileAction action, Callback<NBTTagCompound> callback)
	{
		if(Laptop.getPos() != null)
		{
			Task task = new TaskSendAction(action, Laptop.getPos());
			task.setCallback(callback);
			TaskManager.sendTask(task);
		}
	}

	public boolean readAction(FileAction action)
	{
		System.out.println(action.data.getString("directory"));
		ServerFolder folder = getFolder(action.data.getString("directory"), false);
		if(folder != null)
		{
			NBTTagCompound data = action.data.getCompoundTag("data");
			switch(action.type)
			{
				case NEW:
					if(data.hasKey("files", Constants.NBT.TAG_COMPOUND))
					{
						return folder.add(ServerFolder.fromTag(action.data.getString("file_name"), data), action.data.getBoolean("override"));
					}
					else
					{
						return folder.add(ServerFile.fromTag(action.data.getString("file_name"), data), data.getBoolean("override"));
					}
				case DELETE:
					return folder.delete(action.data.getString("file_name"));
				case RENAME:
					ServerFile file = folder.getFile(action.data.getString("file_name"));
					if(file != null) file.rename(action.data.getString("new_file_name"));
					break;
				case DATA:

			}
		}
		return false;
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

	private static class FileAction
	{
		private Type type;
		private NBTTagCompound data;

		private FileAction(Type type, NBTTagCompound data)
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

		public static FileAction fromTag(NBTTagCompound tag)
		{
			Type type = Type.values()[tag.getInteger("type")];
			NBTTagCompound data = tag.getCompoundTag("data");
			return new FileAction(type, data);
		}

		public enum Type
		{
			NEW, DELETE, RENAME, DATA;
		}
	}

	public static class FileActionFactory
	{
		public static FileAction makeNew(Folder parent, File file, boolean override)
		{
			NBTTagCompound vars = new NBTTagCompound();
			vars.setString("directory", parent.getPath());
			vars.setString("file_name", file.getName());
			vars.setBoolean("override", override);
			vars.setTag("data", file.toTag());
			return new FileAction(FileAction.Type.NEW, vars);
		}

		public static FileAction makeDelete(File file)
		{
			NBTTagCompound vars = new NBTTagCompound();
			vars.setString("directory", file.getLocation());
			vars.setString("file_name", file.getName());
			return new FileAction(FileAction.Type.DELETE, vars);
		}

		public static FileAction makeRename(File file, String newFileName)
		{
			NBTTagCompound vars = new NBTTagCompound();
			vars.setString("directory", createDir(file));
			vars.setString("file_name", file.getName());
			vars.setString("new_file_name", newFileName);
			return new FileAction(FileAction.Type.RENAME, vars);
		}

		public static FileAction makeData(File file, NBTTagCompound data)
		{
			NBTTagCompound vars = new NBTTagCompound();
			vars.setString("directory", file.getPath());
			vars.setString("file_name", file.getName());
			vars.setTag("data", data);
			return new FileAction(FileAction.Type.DATA, vars);
		}

		private static String createDir(File file)
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

	public static class TaskSendAction extends Task
	{
		private FileAction action;
		private BlockPos pos;

		private TaskSendAction()
		{
			super("send_action");
		}

		public TaskSendAction(FileAction action, BlockPos pos)
		{
			this();
			this.action = action;
			this.pos = pos;
		}

		@Override
		public void prepareRequest(NBTTagCompound nbt)
		{
			nbt.setTag("action", action.toTag());
			nbt.setLong("pos", pos.toLong());
		}

		@Override
		public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
		{
			FileAction action = FileAction.fromTag(nbt.getCompoundTag("action"));
			TileEntity tileEntity = world.getTileEntity(BlockPos.fromLong(nbt.getLong("pos")));
			if(tileEntity instanceof TileEntityLaptop)
			{
				TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
				if(laptop.getFileSystem().readAction(action))
				{
					this.setSuccessful();
				}
			}
		}

		@Override
		public void prepareResponse(NBTTagCompound nbt)
		{

		}

		@Override
		public void processResponse(NBTTagCompound nbt)
		{

		}
	}
}
