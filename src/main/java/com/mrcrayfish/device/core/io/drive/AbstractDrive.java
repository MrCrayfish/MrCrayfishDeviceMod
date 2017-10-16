package com.mrcrayfish.device.core.io.drive;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.ServerFile;
import com.mrcrayfish.device.core.io.ServerFolder;
import com.mrcrayfish.device.core.io.action.FileAction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractDrive
{
    protected String name;
    protected UUID uuid;
    protected ServerFolder root;

    AbstractDrive() {}

    AbstractDrive(String name)
    {
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.root = createProtectedFolder("Root");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public ServerFolder getRoot(World world)
    {
        return root;
    }

    public FileSystem.Response handleFileAction(FileAction action, World world)
    {
        NBTTagCompound actionData = action.getData();
        ServerFolder folder = getFolder(actionData.getString("directory"));
        if(folder != null)
        {
            if(folder.getRevision() != actionData.getInteger("directory_revision"))
            {
                return FileSystem.createResponse(FileSystem.Status.FILE_DESYNCED, "Parent folder out of sync. Please refresh");
            }
            NBTTagCompound data = actionData.getCompoundTag("data");
            switch(action.getType())
            {
                case NEW:
                    if(data.hasKey("files", Constants.NBT.TAG_COMPOUND))
                    {
                        return folder.add(ServerFolder.fromTag(actionData.getString("file_name"), data), actionData.getBoolean("override"));
                    }
                    else
                    {
                        return folder.add(ServerFile.fromTag(actionData.getString("file_name"), data), data.getBoolean("override"));
                    }
                case DELETE:
                    return folder.delete(actionData.getString("file_name"));
                case RENAME:
                    ServerFile file = folder.getFile(actionData.getString("file_name"));
                    if(file != null)
                    {
                        if(file.getRevision() != actionData.getInteger("revision"))
                        {
                            return FileSystem.createResponse(FileSystem.Status.FILE_DESYNCED, "File out of sync. Please refresh!");
                        }
                        return file.rename(actionData.getString("new_file_name"));
                    }
                    break;
                case DATA:
                    file = folder.getFile(actionData.getString("file_name"));
                    if(file != null)
                    {
                        if(file.getRevision() != actionData.getInteger("revision"))
                        {
                            return FileSystem.createResponse(FileSystem.Status.FILE_DESYNCED, "File out of sync. Please refresh!");
                        }
                        return file.setData(actionData.getCompoundTag("data"));
                    }
                    break;
            }
        }
        return FileSystem.createResponse(FileSystem.Status.DRIVE_UNAVAILABLE, "Invalid directory");
    }

    public abstract NBTTagCompound toTag();

    public abstract Type getType();

    /**
     * Gets a folder in the file system. To get sub folders, simply use a
     * '/' between each folder name. If the folder does not exist, it will
     * return null.
     *
     * @param path the directory of the folder
     */
    @Nullable
    public ServerFolder getFolder(String path)
    {
        if(path == null)
            throw new IllegalArgumentException("The path can not be null");

        if(!FileSystem.PATTERN_DIRECTORY.matcher(path).matches())
            throw new IllegalArgumentException("The path \"" + path + "\" does not follow the correct format");

        if(path.equals("/"))
            return root;

        ServerFolder prev = root;
        String[] folders = path.split("/");
        if(folders.length > 0 && folders.length <= 10)
        {
            for(int i = 1; i < folders.length; i++)
            {
                ServerFolder temp = prev.getFolder(folders[i]);
                if(temp == null) return null;
                prev = temp;
            }
            return prev;
        }
        return null;
    }

    public ServerFolder getDriveStructure()
    {
        return root.copyStructure();
    }

    private static ServerFolder createProtectedFolder(String name)
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

    public enum Type
    {
        INTERNAL, EXTERNAL, NETWORK;
    }
}
