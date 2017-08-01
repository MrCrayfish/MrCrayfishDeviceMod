package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TaskUpdateFileSystem extends Task
{
    private int x, y, z;
    private NBTTagCompound tag;

    public TaskUpdateFileSystem()
    {
        super("update_file_system");
    }

    public TaskUpdateFileSystem(BlockPos pos, NBTTagCompound tag)
    {
        this();
        this.x = pos.getX();
        this.y = pos.getZ();
        this.z = pos.getZ();
        this.tag = tag;
    }

    @Override
    public void prepareRequest(NBTTagCompound nbt)
    {
        nbt.setInteger("x", this.x);
        nbt.setInteger("y", this.y);
        nbt.setInteger("z", this.z);
        nbt.setTag("file_system", this.tag);
    }

    @Override
    public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z")));
        if(tileEntity instanceof TileEntityLaptop)
        {
            TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
            laptop.setFileSystemData(nbt.getCompoundTag("file_system"));
        }
    }

    @Override
    public void prepareResponse(NBTTagCompound nbt)
    {

    }

    @Override
    public void processResponse(NBTTagCompound nbt)
    {

    }
}
