package com.mrcrayfish.device.core.network.task;

import com.mrcrayfish.device.api.task.Task;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class TaskConnect extends Task
{
    public TaskConnect()
    {
        super("connect");
    }

    @Override
    public void prepareRequest(NBTTagCompound nbt)
    {

    }

    @Override
    public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
    {

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
