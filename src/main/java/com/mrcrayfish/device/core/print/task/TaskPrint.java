package com.mrcrayfish.device.core.print.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.init.DeviceItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class TaskPrint extends Task
{
    private BlockPos pos;
    private int[] pixels;
    private int resolution;

    private TaskPrint()
    {
        super("print");
    }

    public TaskPrint(BlockPos pos, int[] pixels, int resolution)
    {
        this();
        this.pos = pos;
        this.pixels = pixels;
        this.resolution = resolution;
    }

    @Override
    public void prepareRequest(NBTTagCompound nbt)
    {
        nbt.setLong("pos", pos.toLong());
        nbt.setIntArray("pixels", pixels);
        nbt.setInteger("resolution", resolution);
    }

    @Override
    public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
    {
        BlockPos pos = BlockPos.fromLong(nbt.getLong("pos"));
        ItemStack stack = new ItemStack(DeviceItems.paper_printed);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray("pixels", nbt.getIntArray("pixels"));
        tag.setInteger("resolution", nbt.getInteger("resolution"));
        stack.setTagCompound(tag);
        world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack));
        this.setSuccessful();
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
