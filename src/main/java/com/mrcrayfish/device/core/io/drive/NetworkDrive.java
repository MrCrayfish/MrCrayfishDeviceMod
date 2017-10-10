package com.mrcrayfish.device.core.io.drive;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.ServerFolder;
import com.mrcrayfish.device.core.io.action.FileAction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    }

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
    public FileSystem.Response handleFileAction(FileAction action, World world)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof Interface)
        {
            Interface impl = (Interface) tileEntity;
            AbstractDrive drive = impl.getDrive();
            if(drive.handleFileAction(action, world).getStatus() == FileSystem.Status.SUCCESSFUL)
            {
                tileEntity.markDirty();
                return FileSystem.createSuccessResponse();
            }
        }
        return FileSystem.createResponse(FileSystem.Status.DRIVE_NETWORK_MISSING, "The network drive could not be found");
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
