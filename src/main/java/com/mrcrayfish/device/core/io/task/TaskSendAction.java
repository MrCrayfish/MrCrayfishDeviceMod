package com.mrcrayfish.device.core.io.task;

import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class TaskSendAction extends Task
{
    private String uuid;
    private FileAction action;
    private BlockPos pos;

    private FileSystem.Response response;

    private TaskSendAction()
    {
        super("send_action");
    }

    public TaskSendAction(Drive drive, FileAction action)
    {
        this();
        this.uuid = drive.getUUID().toString();
        this.action = action;
        this.pos = Laptop.getPos();
    }

    @Override
    public void prepareRequest(NBTTagCompound nbt)
    {
        nbt.setString("uuid", uuid);
        nbt.setTag("action", action.toTag());
        nbt.setLong("pos", pos.toLong());
    }

    @Override
    public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
    {
        FileAction action = FileAction.fromTag(nbt.getCompoundTag("action"));
        TileEntity tileEntity = world.getTileEntity(BlockPos.fromLong(nbt.getLong("pos")));
        if(tileEntity instanceof TileEntityLaptop)
        {
            TileEntityLaptop laptop = (TileEntityLaptop) tileEntity;
            response = laptop.getFileSystem().readAction(nbt.getString("uuid"), action, world);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(NBTTagCompound nbt)
    {
        nbt.setTag("response", response.toTag());
    }

    @Override
    public void processResponse(NBTTagCompound nbt)
    {

    }
}
