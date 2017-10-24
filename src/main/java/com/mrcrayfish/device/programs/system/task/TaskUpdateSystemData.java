package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TaskUpdateSystemData extends Task
{
    private int x, y, z;
    private NBTTagCompound data;

    public TaskUpdateSystemData()
    {
        super("update_application_data");
    }

    public TaskUpdateSystemData(BlockPos pos, @Nonnull NBTTagCompound data)
    {
        this();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.data = data;
    }

    @Override
    public void prepareRequest(NBTTagCompound tag)
    {
        tag.setInteger("posX", this.x);
        tag.setInteger("posY", this.y);
        tag.setInteger("posZ", this.z);
        tag.setTag("data", this.data);
    }

    @Override
    public void processRequest(NBTTagCompound tag, World world, EntityPlayer player)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(tag.getInteger("posX"), tag.getInteger("posY"), tag.getInteger("posZ")));
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
