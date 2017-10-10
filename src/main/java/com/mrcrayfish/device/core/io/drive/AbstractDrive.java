package com.mrcrayfish.device.core.io.drive;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.ServerFile;
import com.mrcrayfish.device.core.io.ServerFolder;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Author: MrCrayfish
 */
public abstract class AbstractDrive
{
    protected String name;
    protected ServerFolder root;

    AbstractDrive(String name)
    {
        this.name = name;
        this.root = createProtectedFolder("Root");
    }

    AbstractDrive() {}

    public String getName()
    {
        return name;
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
                        return file.rename(actionData.getString("new_file_name"));
                    }
                    break;
                case DATA:

            }
        }
        return FileSystem.createResponse(FileSystem.Status.DRIVE_MISSING, "Invalid directory");
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
    public final ServerFolder getFolder(String path)
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
