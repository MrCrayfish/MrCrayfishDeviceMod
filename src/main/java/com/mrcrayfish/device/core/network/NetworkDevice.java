package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.tileentity.TileEntityNetworkDevice;
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
        if(tileEntity instanceof TileEntityNetworkDevice)
        {
            TileEntityNetworkDevice device = (TileEntityNetworkDevice) tileEntity;
            return device.getUUID().equals(id);
        }
        return false;
    }

    public void update(NetworkClient device)
    {
        //this.pos = device.getPos();
    }
}
