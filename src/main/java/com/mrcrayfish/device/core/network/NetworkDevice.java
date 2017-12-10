package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.tileentity.TileEntityDevice;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class NetworkDevice
{
    private UUID id;
    private String name;
    private BlockPos pos;

    private NetworkDevice() {}

    public NetworkDevice(NetworkClient device)
    {
        this.id = device.getUUID();
        //this.name = device.getDeviceName();
        //this.pos = device.getPos();
    }

    public UUID getUUID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public boolean isConnected(World world)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof TileEntityDevice)
        {
            TileEntityDevice device = (TileEntityDevice) tileEntity;
            return device.getId().equals(id);
        }
        return false;
    }

    public void update(NetworkClient device)
    {
        //this.pos = device.getPos();
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", id.toString());
        tag.setString("name", name);
        tag.setLong("pos", pos.toLong());
        return tag;
    }

    public static NetworkDevice fromTag(NBTTagCompound tag)
    {
        NetworkDevice device = new NetworkDevice();
        device.id = UUID.fromString(tag.getString("id"));
        device.name = tag.getString("name");
        device.pos = BlockPos.fromLong(tag.getLong("pos"));
        return device;
    }
}
