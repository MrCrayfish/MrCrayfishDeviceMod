package com.mrcrayfish.device.core.io;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.io.DataException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class ServerFolder extends ServerFile
{
    private List<ServerFile> files = new ArrayList<>();

    public ServerFolder(String name)
    {
        this(name, false);
    }

    public ServerFolder(String name, boolean protect)
    {
        this.name = name;
        this.protect = protect;
    }

    public boolean add(ServerFile file, boolean override)
    {
        if(file == null)
            throw new IllegalArgumentException("A null file can not be added to a ServerFolder");

        if(hasFile(file.name))
        {
            if(!override || getFile(file.name).isProtected()) return false;
            files.remove(getFile(file.name));
        }

        files.add(file);
        file.parent = this;
        return true;
    }

    public boolean delete(String name)
    {
        return delete(getFile(name));
    }

    public boolean delete(ServerFile file)
    {
        if(file != null)
        {
            if(file.isProtected()) return false;

            file.parent = null;
            files.remove(file);
            return true;
        }
        return false;
    }

    public boolean hasFile(String name)
    {
        return files.stream().anyMatch(file -> file.name.equalsIgnoreCase(name));
    }

    @Nullable
    public ServerFile getFile(String name)
    {
        return files.stream().filter(file -> file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean hasFolder(String name)
    {
        return files.stream().anyMatch(file -> file.isFolder() && file.name.equalsIgnoreCase(name));
    }

    @Nullable
    public ServerFolder getFolder(String name)
    {
        return (ServerFolder) files.stream().filter(file -> file.isFolder() && file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<ServerFile> getFiles()
    {
        return files;
    }

    public List<ServerFile> search(Predicate<ServerFile> conditions, boolean includeSubServerFolders)
    {
        List<ServerFile> found = NonNullList.create();
        search(found, conditions, includeSubServerFolders);
        return found;
    }

    private void search(List<ServerFile> results, Predicate<ServerFile> conditions, boolean includeSubServerFolders)
    {
        files.stream().forEach(file ->
        {
            if(file.isFolder())
            {
                if(includeSubServerFolders)
                {
                    ((ServerFolder) file).search(results, conditions, includeSubServerFolders);
                }
            }
            else if(conditions.test(file))
            {
                results.add(file);
            }
        });
    }

    public void setFiles(List<ServerFile> files)
    {
        this.files = files;
    }

    @Override
    public boolean isFolder()
    {
        return true;
    }

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

    public static ServerFolder fromTag(String name, NBTTagCompound ServerFolderTag)
    {
        ServerFolder folder = new ServerFolder(name);

        if(ServerFolderTag.hasKey("protected", Constants.NBT.TAG_BYTE))
            folder.protect = ServerFolderTag.getBoolean("protected");

        NBTTagCompound fileList = ServerFolderTag.getCompoundTag("files");
        for(String fileName : fileList.getKeySet())
        {
            NBTTagCompound fileTag = fileList.getCompoundTag(fileName);
            if(fileTag.hasKey("files"))
            {
                folder.add(ServerFolder.fromTag(fileName, fileTag), false);
            }
            else
            {
                folder.add(ServerFile.fromTag(fileName, fileTag), false);
            }
        }
        return folder;
    }

    @Override
    public void setData(@Nonnull NBTTagCompound data)
    {
        throw new DataException("Data can not be set to a ServerFolder");
    }

    @Override
    public ServerFile copy()
    {
        ServerFolder folder = new ServerFolder(name);
        files.forEach(f ->
        {
            ServerFile copy = f.copy();
            copy.protect = false;
            folder.add(copy, false);
        });
        return folder;
    }

    public ServerFolder copyStructure()
    {
        ServerFolder folder = new ServerFolder(name, protect);
        files.forEach(f ->
        {
            if(f.isFolder())
            {
                folder.add(((ServerFolder)f).copyStructure(), false);
            }
        });
        return folder;
    }

    public void print(int startingDepth)
    {
        String indent = "";
        for(int i = 0; i < startingDepth; i++)
        {
            indent += "  ";
        }
        MrCrayfishDeviceMod.getLogger().info(indent + "⌊ " + name);
        for(ServerFile file : files)
        {
            if(file.isFolder())
            {
                ((ServerFolder) file).print(startingDepth + 1);
            }
            else
            {
                MrCrayfishDeviceMod.getLogger().info(indent + "  ⌊ " + file.name);
            }
        }
    }
}
