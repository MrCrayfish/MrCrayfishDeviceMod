package com.mrcrayfish.device.core.io.task;

import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class TaskGetMainDrive extends Task
{
    private BlockPos pos;

    private AbstractDrive mainDrive;

    private TaskGetMainDrive()
    {
        super("get_main_drive");
    }

    public TaskGetMainDrive(BlockPos pos)
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
            mainDrive = fileSystem.getMainDrive();
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(NBTTagCompound nbt)
    {
        if(this.isSucessful())
        {
            NBTTagCompound mainDriveTag = new NBTTagCompound();
            mainDriveTag.setString("name", mainDrive.getName());
            mainDriveTag.setString("uuid", mainDrive.getUUID().toString());
            mainDriveTag.setString("type", mainDrive.getType().toString());
            nbt.setTag("main_drive", mainDriveTag);
            nbt.setTag("structure", mainDrive.getDriveStructure().toTag());
        }
    }

    @Override
    public void processResponse(NBTTagCompound nbt)
    {
        if(this.isSucessful())
        {
            if(Minecraft.getMinecraft().currentScreen instanceof Laptop)
            {
                NBTTagCompound structureTag = nbt.getCompoundTag("structure");
                Drive drive = new Drive(nbt.getCompoundTag("main_drive"));
                drive.syncRoot(Folder.fromTag(FileSystem.LAPTOP_DRIVE_NAME, structureTag));
                drive.getRoot().validate();

                if(Laptop.getMainDrive() == null)
                {
                    Laptop.setMainDrive(drive);
                }
            }
        }
    }
}
