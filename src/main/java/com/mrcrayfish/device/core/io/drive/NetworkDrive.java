package com.mrcrayfish.device.core.io.drive;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.ServerFolder;
import com.mrcrayfish.device.core.io.action.FileAction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public final class NetworkDrive extends AbstractDrive
{
    private BlockPos pos;

    public NetworkDrive(String name, BlockPos pos)
    {
        super(name);
        this.pos = pos;
        this.root = null;
    }

    @Nullable
    @Override
    public ServerFolder getRoot(World world)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof Interface)
        {
            Interface impl = (Interface) tileEntity;
            AbstractDrive drive = impl.getDrive();
            if(drive != null)
            {
                return drive.getRoot(world);
            }
        }
        return null;
    }

    @Override
    public FileSystem.Response handleFileAction(FileSystem fileSystem, FileAction action, World world)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof Interface)
        {
            Interface impl = (Interface) tileEntity;
            AbstractDrive drive = impl.getDrive();
            if(drive.handleFileAction(fileSystem, action, world).getStatus() == FileSystem.Status.SUCCESSFUL)
            {
                tileEntity.markDirty();
                return FileSystem.createSuccessResponse();
            }
        }
        return FileSystem.createResponse(FileSystem.Status.DRIVE_UNAVAILABLE, "The network drive could not be found");
    }

    @Nullable
    @Override
    public ServerFolder getFolder(String path)
    {
        return null;
    }

    @Override
    public Type getType()
    {
        return Type.NETWORK;
    }

    @Override
    public NBTTagCompound toTag()
    {
        return null;
    }

    public interface Interface
    {
        AbstractDrive getDrive();

        boolean canAccessDrive();
    }
}
