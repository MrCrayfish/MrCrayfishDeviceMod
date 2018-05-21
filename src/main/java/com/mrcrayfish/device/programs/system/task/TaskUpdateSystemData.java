package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.annotation.DeviceTask;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@DeviceTask(modId = Reference.MOD_ID, taskId = "update_system_data")
public class TaskUpdateSystemData extends Task
{
    private BlockPos pos;
    private NBTTagCompound data;

    public TaskUpdateSystemData() {}

    public TaskUpdateSystemData(BlockPos pos, NBTTagCompound data)
    {
        this();
        this.pos = pos;
        this.data = data;
    }

    @Override
    public void prepareRequest(NBTTagCompound tag)
    {
        tag.setLong("pos", pos.toLong());
        tag.setTag("data", this.data);
    }

    @Override
    public void processRequest(NBTTagCompound tag, World world, EntityPlayer player)
    {
        BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof TileEntityLaptop)
        {
            TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
            laptop.setSystemData(tag.getCompoundTag("data"));
        }
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(NBTTagCompound tag)
    {

    }

    @Override
    public void processResponse(NBTTagCompound tag)
    {

    }
}
