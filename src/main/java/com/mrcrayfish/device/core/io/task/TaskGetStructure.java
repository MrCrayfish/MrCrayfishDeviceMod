package com.mrcrayfish.device.core.io.task;

import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.ServerFolder;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class TaskGetStructure extends Task
{
    private String uuid;
    private BlockPos pos;

    private ServerFolder folder;

    private TaskGetStructure()
    {
        super("get_folder_structure");
    }

    public TaskGetStructure(Drive drive, BlockPos pos)
    {
        this();
        this.uuid = drive.getUUID().toString();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(NBTTagCompound nbt)
    {
        nbt.setString("uuid", uuid);
        nbt.setLong("pos", pos.toLong());
    }

    @Override
    public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
    {
        TileEntity tileEntity = world.getTileEntity(BlockPos.fromLong(nbt.getLong("pos")));
        if(tileEntity instanceof TileEntityLaptop)
        {
            TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
            FileSystem fileSystem = laptop.getFileSystem();
            UUID uuid = UUID.fromString(nbt.getString("uuid"));
            AbstractDrive serverDrive = fileSystem.getAvailableDrives(world, true).get(uuid);
            if(serverDrive != null)
            {
                folder = serverDrive.getDriveStructure();
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(NBTTagCompound nbt)
    {
        if(folder != null)
        {
            nbt.setString("file_name", folder.getName());
            nbt.setTag("structure", folder.toTag());
        }
    }

    @Override
    public void processResponse(NBTTagCompound nbt)
    {

    }
}
