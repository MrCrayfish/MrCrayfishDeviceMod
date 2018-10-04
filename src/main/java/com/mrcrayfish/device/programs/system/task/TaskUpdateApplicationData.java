package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TaskUpdateApplicationData extends Task
{
    private BlockPos pos;
    private String appId;
    private NBTTagCompound data;

    public TaskUpdateApplicationData()
    {
        super("update_application_data");
    }

    public TaskUpdateApplicationData(BlockPos pos, String appId, NBTTagCompound data)
    {
        this();
        this.pos = pos;
        this.appId = appId;
        this.data = data;
    }

    @Override
    public void prepareRequest(NBTTagCompound tag)
    {
        tag.setLong("pos", this.pos.toLong());
        tag.setString("appId", this.appId);
        tag.setTag("appData", this.data);
    }

    @Override
    public void processRequest(NBTTagCompound tag, World world, EntityPlayer player)
    {
    	BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof TileEntityLaptop)
        {
            TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
            laptop.setApplicationData(tag.getString("appId"), tag.getCompoundTag("appData"));
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
