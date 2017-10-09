package com.mrcrayfish.device.core.io;

import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.core.io.drive.InternalDrive;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.core.io.drive.NetworkDrive;
import com.mrcrayfish.device.core.io.task.TaskSendAction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

public class FileSystem
{
	public static final Pattern PATTERN_DIRECTORY = Pattern.compile("^(/)|(/[a-zA-Z0-9 ]{1,32})*$");

	public static final String DIR_ROOT = "/";
	public static final String DIR_APPLICATION_DATA = DIR_ROOT + "Application Data";
	public static final String DIR_HOME = DIR_ROOT + "Home";

	public static final String LAPTOP_DRIVE_NAME = "Root";

	private final Map<String, AbstractDrive> DRIVES = new LinkedHashMap<>();

	private TileEntity tileEntity;
	
	public FileSystem(TileEntity tileEntity, NBTTagCompound fileSystemTag)
	{
		this.tileEntity = tileEntity;
		load(fileSystemTag);
	}

	private void load(NBTTagCompound fileSystemTag)
	{
		if(fileSystemTag.hasKey("drives", Constants.NBT.TAG_LIST))
		{
			NBTTagList tagList = fileSystemTag.getTagList("drives", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < tagList.tagCount(); i++)
			{
				NBTTagCompound driveTag = tagList.getCompoundTagAt(i);
				AbstractDrive drive = InternalDrive.fromTag(driveTag);
				DRIVES.put(driveTag.getString("name"), drive);
			}
		}
		setupDefault();
	}

	/**
	 * Sets up the default folders for the file system if they don't exist.
	 */
	private void setupDefault()
	{
		if(!DRIVES.containsKey(FileSystem.LAPTOP_DRIVE_NAME))
		{
			DRIVES.put(FileSystem.LAPTOP_DRIVE_NAME, new InternalDrive(FileSystem.LAPTOP_DRIVE_NAME));
			tileEntity.markDirty();
		}

		AbstractDrive drive = DRIVES.get(FileSystem.LAPTOP_DRIVE_NAME);
		ServerFolder root = drive.getRoot(tileEntity.getWorld());

		if(!root.hasFolder("Home"))
		{
			root.add(createProtectedFolder("Home"), false);
			tileEntity.markDirty();
		}

		if(!root.hasFolder("Application Data"))
		{
			root.add(createProtectedFolder("Application Data"), false);
			tileEntity.markDirty();
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

	@SideOnly(Side.CLIENT)
	public static void sendAction(Drive drive, FileAction action, Callback<NBTTagCompound> callback)
	{
		if(Laptop.getPos() != null)
		{
			Task task = new TaskSendAction(drive, action, Laptop.getPos());
			task.setCallback(callback);
			TaskManager.sendTask(task);
		}
	}

	public boolean readAction(String drive, FileAction action)
	{
		return false;
	}

	@Nullable
	public AbstractDrive getDrive(String name)
	{
		return DRIVES.get(name);
	}

	public List<AbstractDrive> getInternalDrives()
	{
		return new ArrayList<>(DRIVES.values());
	}

	public Map<String, AbstractDrive> getAvailableDrives(World world)
	{
		Map<String, AbstractDrive> drives = new HashMap<>();
		DRIVES.forEach((k, v) -> drives.put(k, v));
		//TODO add usb
		//TODO add network drives
		return drives;
	}

	public NBTTagCompound toTag()
	{
		NBTTagCompound fileSystemTag = new NBTTagCompound();
		NBTTagList tagList = new NBTTagList();
		DRIVES.keySet().forEach(driveName -> {
			NBTTagCompound driveTag = new NBTTagCompound();
			driveTag.setString("name", driveName);
			driveTag.setTag("root", DRIVES.get(driveName).toTag());
		});
		fileSystemTag.setTag("drives", tagList);
		return fileSystemTag;
	}


}
