package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.core.Device;
import com.mrcrayfish.device.tileentity.TileEntityNetworkDevice;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class NetworkDevice extends Device
{
    private NetworkDevice() {}

    public NetworkDevice(TileEntityNetworkDevice device)
    {
        super(device);
    }

    public NetworkDevice(UUID id, String name, Router router)
    {
        super(id, name);
    }

    public boolean isConnected(World world)
    {
        if(pos == null)
            return false;

        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof TileEntityNetworkDevice)
        {
            TileEntityNetworkDevice device = (TileEntityNetworkDevice) tileEntity;
            Router router = device.getRouter();
            return router != null && router.getId().equals(router.getId());
        }
        return false;
    }

    @Nullable
    @Override
    public TileEntityNetworkDevice getDevice(World world)
    {
        if(pos == null)
            return null;

        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof TileEntityNetworkDevice)
        {
            TileEntityNetworkDevice tileEntityNetworkDevice = (TileEntityNetworkDevice) tileEntity;
            if(tileEntityNetworkDevice.getId().equals(getId()))
            {
                return tileEntityNetworkDevice;
            }
        }
        return null;
    }

    public NBTTagCompound toTag(boolean includePos)
    {
        NBTTagCompound tag = super.toTag(includePos);
        if(includePos && pos != null)
        {
            tag.setLong("pos", pos.toLong());
        }
        return tag;
    }

    public static NetworkDevice fromTag(NBTTagCompound tag)
    {
        NetworkDevice device = new NetworkDevice();
        device.id = UUID.fromString(tag.getString("id"));
        device.name = tag.getString("name");
        if(tag.hasKey("pos", Constants.NBT.TAG_LONG))
        {
            device.pos = BlockPos.fromLong(tag.getLong("pos"));
        }
        return device;
    }
}
