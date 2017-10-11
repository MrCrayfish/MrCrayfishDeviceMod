package com.mrcrayfish.device.core.io;

import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.core.io.drive.ExternalDrive;
import com.mrcrayfish.device.core.io.drive.InternalDrive;
import com.mrcrayfish.device.core.io.task.TaskSendAction;
import net.minecraft.item.ItemStack;
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
	private AbstractDrive attachedDrive = null; //USB

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
				AbstractDrive drive = InternalDrive.fromTag(driveTag.getCompoundTag("drive"));
				DRIVES.put(driveTag.getString("name"), drive);
			}
		}

		if(fileSystemTag.hasKey("external_drive", Constants.NBT.TAG_COMPOUND))
		{
			attachedDrive = ExternalDrive.fromTag(fileSystemTag.getCompoundTag("external_drive"));
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
	public static void sendAction(Drive drive, FileAction action, @Nullable Callback<Response> callback)
	{
		if(Laptop.getPos() != null)
		{
			Task task = new TaskSendAction(drive, action);
			task.setCallback((nbt, success) ->
			{
				if(callback != null)
				{
					callback.execute(Response.fromTag(nbt), success);
				}
            });
			TaskManager.sendTask(task);
		}
	}

	public Response readAction(String driveName, FileAction action, World world)
	{
		AbstractDrive drive = getAvailableDrives(world).get(driveName);
		if(drive != null)
		{
			Response response = drive.handleFileAction(action, world);
			if(response.getStatus() == Status.SUCCESSFUL)
			{
				tileEntity.markDirty();
			}
			return response;
		}
		return createResponse(Status.DRIVE_MISSING, "Drive unavailable or missing");
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
		//Internal
		DRIVES.forEach((k, v) -> drives.put(k, v));
		//External
		if(attachedDrive != null)
			drives.put(attachedDrive.getName(), attachedDrive);
		//TODO add network drives
		return drives;
	}

	public NBTTagCompound toTag()
	{
		NBTTagCompound fileSystemTag = new NBTTagCompound();

		NBTTagList tagList = new NBTTagList();
		DRIVES.forEach((k, v) -> {
			NBTTagCompound driveTag = new NBTTagCompound();
			driveTag.setString("name", k);
			driveTag.setTag("drive", v.toTag());
			tagList.appendTag(driveTag);
		});
		fileSystemTag.setTag("drives", tagList);

		if(attachedDrive != null)
		{
			fileSystemTag.setTag("external_drive", attachedDrive.toTag());
		}
		return fileSystemTag;
	}

	public boolean setAttachedDrive(ItemStack flashDrive)
	{
		if(attachedDrive == null)
		{
			NBTTagCompound flashDriveTag = getExternalDriveTag(flashDrive);
			attachedDrive = ExternalDrive.fromTag(flashDriveTag.getCompoundTag("drive"));
			return true;
		}
		return false;
	}

	public AbstractDrive getAttachedDrive()
	{
		return attachedDrive;
	}

	private NBTTagCompound getExternalDriveTag(ItemStack stack)
	{
		NBTTagCompound tagCompound = stack.getTagCompound();
		if(tagCompound == null)
		{
			NBTTagCompound flashDriveTag = new NBTTagCompound();
			flashDriveTag.setTag("drive", new ExternalDrive(stack.getDisplayName()).toTag());
			stack.setTagCompound(flashDriveTag);
		}
		else if(!tagCompound.hasKey("drive", Constants.NBT.TAG_COMPOUND))
		{
			tagCompound.setTag("drive", new ExternalDrive(stack.getDisplayName()).toTag());
		}
		return tagCompound;
	}

	public static Response createSuccessResponse()
	{
		return new Response(Status.SUCCESSFUL);
	}

	public static Response createResponse(int status, String message)
	{
		return new Response(status, message);
	}

	public static class Response
	{
		private final int status;
		private String message = "";

		private Response(int status)
		{
			this.status = status;
		}

		private Response(int status, String message)
		{
			this.status = status;
			this.message = message;
		}

		public int getStatus()
		{
			return status;
		}

		public String getMessage()
		{
			return message;
		}

		public NBTTagCompound toTag()
		{
			NBTTagCompound responseTag = new NBTTagCompound();
			responseTag.setInteger("status", status);
			responseTag.setString("message", message);
			return responseTag;
		}

		public static Response fromTag(NBTTagCompound responseTag)
		{
			return new Response(responseTag.getInteger("status"), responseTag.getString("message"));
		}
	}

	public static class Status
	{
		public static final int SUCCESSFUL = 0;
		public static final int FAILED = 1;
		public static final int FILE_ILLEGAL = 1;
		public static final int FILE_IS_PROTECTED = 1;
		public static final int FILE_EXISTS = 1;
		public static final int FILE_INVALID_DATA = 1;
		public static final int FILE_EMPTY_NAME = 1;
		public static final int DRIVE_MISSING = 1;
		public static final int DRIVE_NETWORK_MISSING = 1;
	}
}
