package com.mrcrayfish.device.core.io.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class TaskGetFileSystem extends Task
{
    private BlockPos pos;

    private Map<String, AbstractDrive> drives;

    private TaskGetFileSystem()
    {
        super("get_file_system");
    }

    public TaskGetFileSystem(BlockPos pos)
    {
        this();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(NBTTagCompound nbt)
    {
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
            drives = fileSystem.getAvailableDrives(world);
            if(drives.containsKey(FileSystem.LAPTOP_DRIVE_NAME))
            {
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(NBTTagCompound nbt)
    {
        if(this.isSucessful())
        {
            NBTTagList driveList = new NBTTagList();
            drives.forEach((k, v) -> {
                NBTTagCompound driveTag = new NBTTagCompound();
                driveTag.setString("name", v.getName());
                driveTag.setString("type", v.getType().toString());
                driveList.appendTag(driveTag);
            });
            nbt.setTag("drives", driveList);

            AbstractDrive rootDrive = drives.get(FileSystem.LAPTOP_DRIVE_NAME);
            nbt.setTag("structure", rootDrive.getDriveStructure().toTag());
        }
    }

    @Override
    public void processResponse(NBTTagCompound nbt)
    {

    }
}
