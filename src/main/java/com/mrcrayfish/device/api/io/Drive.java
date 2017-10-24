package com.mrcrayfish.device.api.io;

import com.mrcrayfish.device.core.io.FileSystem;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class Drive
{
    private String name;
    private UUID uuid;
    private Type type;
    private Folder root;

    private boolean synced = false;

    public Drive(NBTTagCompound driveTag)
    {
        this.name = driveTag.getString("name");
        this.uuid = UUID.fromString(driveTag.getString("uuid"));
        this.type = Type.fromString(driveTag.getString("type"));
    }

    /**
     * Gets the name of the Drive.
     *
     * @return the drive name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the UUID of the Drive.
     *
     * @return the drive uuid
     */
    public UUID getUUID()
    {
        return uuid;
    }

    /**
     * Gets the {@link Type} of the Drive. This is either internal, external or network. Used for
     * determining the icon.
     *
     * @return the drive type
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Gets the root {@link Folder} of this Drive
     *
     * @return
     */
    public Folder getRoot()
    {
        return root;
    }

    /**
     * Do not use! Sync the drive structure to a folder
     *
     * @param root the root folder of this drive
     */
    public void syncRoot(Folder root)
    {
        if(!synced)
        {
            this.root = root;
            root.setDrive(this);
            root.validate();
            synced = true;
        }
    }

    /**
     * Do not use! Checks if the drive structure is synced
     *
     * @return is drive structure synced
     */
    public boolean isSynced()
    {
        return synced;
    }

    /**
     * Gets a folder in the file system. To get sub folders, simply use a
     * '/' between each folder name. If the folder does not exist, it will
     * return null.
     *
     * @param path the directory of the folder
     */
    @Nullable
    public final Folder getFolder(String path)
    {
        if(path == null)
            throw new IllegalArgumentException("The path can not be null");

        if(!FileSystem.PATTERN_DIRECTORY.matcher(path).matches())
            throw new IllegalArgumentException("The path \"" + path + "\" does not follow the correct format");

        if(path.equals("/"))
            return root;

        Folder prev = root;
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

    @Override
    public String toString()
    {
        return name;
    }

    public enum Type
    {
        INTERNAL, EXTERNAL, NETWORK, UNKNOWN;

        public static Type fromString(String type)
        {
            for(Type t : values())
            {
                if(t.toString().equals(type))
                {
                    return t;
                }
            }
            return UNKNOWN;
        }
    }
}
