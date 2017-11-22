package com.mrcrayfish.device.core.print.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
        TileEntity tileEntity = world.getTileEntity(BlockPos.fromLong(nbt.getLong("pos")));
        if(tileEntity instanceof TileEntityPrinter)
        {
            TileEntityPrinter printer = (TileEntityPrinter) tileEntity;

            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("type", "cdm:picture");
            NBTTagCompound data = new NBTTagCompound();
            data.setIntArray("pixels", nbt.getIntArray("pixels"));
            data.setInteger("resolution", nbt.getInteger("resolution"));
            tag.setTag("data", data);

            ItemStack stack = new ItemStack(DeviceItems.paper_printed);
            stack.setTagCompound(tag);
            printer.print(stack);

            this.setSuccessful();
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
