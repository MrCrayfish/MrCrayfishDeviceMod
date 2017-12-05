package com.mrcrayfish.device.core.io;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.mrcrayfish.device.core.io.FileSystem.Status;

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

    private ServerFolder(String name, boolean protect)
    {
        this.name = name;
        this.protect = protect;
    }

    public FileSystem.Response add(ServerFile file, boolean override)
    {
        if(file == null)
            return FileSystem.createResponse(Status.FILE_INVALID, "Illegal file");

        if(!FileSystem.PATTERN_FILE_NAME.matcher(file.getName()).matches())
            return FileSystem.createResponse(Status.FILE_INVALID_NAME, "Invalid file name");

        if(hasFile(file.name))
        {
            if(!override)
                return FileSystem.createResponse(Status.FILE_EXISTS, "A file with that name already exists");
            if(getFile(file.name).isProtected())
                return FileSystem.createResponse(Status.FILE_IS_PROTECTED, "Unable to override protected files");
            files.remove(getFile(file.name));
        }

        files.add(file);
        file.parent = this;
        return FileSystem.createSuccessResponse();
    }

    public FileSystem.Response delete(String name)
    {
        return delete(getFile(name));
    }

    public FileSystem.Response delete(ServerFile file)
    {
        if(file == null)
            return FileSystem.createResponse(Status.FILE_INVALID, "Illegal file");

        if(!files.contains(file))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "The file does not exist in this folder");

        if(file.isProtected())
            return FileSystem.createResponse(Status.FILE_IS_PROTECTED, "Cannot delete protected files");

        file.parent = null;
        files.remove(file);
        return FileSystem.createSuccessResponse();
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

    public static ServerFolder fromTag(String name, NBTTagCompound folderTag)
    {
        ServerFolder folder = new ServerFolder(name);

        if(folderTag.hasKey("protected", Constants.NBT.TAG_BYTE))
            folder.protect = folderTag.getBoolean("protected");

        NBTTagCompound fileList = folderTag.getCompoundTag("files");
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
    public FileSystem.Response setData(@Nonnull NBTTagCompound data)
    {
        return FileSystem.createResponse(Status.FILE_INVALID_DATA, "Data can not be set to a folder");
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

    /*public void print(int startingDepth)
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
    }*/
}
