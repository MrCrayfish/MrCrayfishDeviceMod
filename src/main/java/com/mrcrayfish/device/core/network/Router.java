package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.tileentity.TileEntityDevice;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.*;

/**
 * Author: MrCrayfish
 */
public class Router implements IDevice
{
    private final Map<UUID, NetworkDevice> NETWORK_DEVICES = new HashMap<>();

    private UUID routerId;
    private BlockPos pos;

    public Router(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public UUID getId()
    {
        if(routerId == null)
        {
            routerId = UUID.randomUUID();
        }
        return routerId;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public void setPos(BlockPos pos)
    {
        this.pos = pos;
    }

    public void addDevice(TileEntityDevice device)
    {
        if(!NETWORK_DEVICES.containsKey(device.getId()))
        {
            NETWORK_DEVICES.put(device.getId(), new NetworkDevice(device, this));
        }
    }

    public void removeDevice(TileEntityDevice device)
    {
        NETWORK_DEVICES.remove(device.getId());
    }

    public Collection<NetworkDevice> getNetworkDevices()
    {
        return NETWORK_DEVICES.values();
    }

    public boolean ping(TileEntityDevice source)
    {
        if(NETWORK_DEVICES.containsKey(source.getId()))
        {
            NETWORK_DEVICES.get(source.getId()).update(source);
            return true;
        }
        return false;
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setUniqueId("id", getId());

        NBTTagList deviceList = new NBTTagList();
        NETWORK_DEVICES.forEach((id, device) -> {
            deviceList.appendTag(device.toTag());
        });
        tag.setTag("network_devices", deviceList);

        return tag;
    }

    public static Router fromTag(BlockPos pos, NBTTagCompound tag)
    {
        Router router = new Router(pos);
        router.routerId = tag.getUniqueId("id");

        NBTTagList deviceList = tag.getTagList("network_devices", Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < deviceList.tagCount(); i++)
        {
            NetworkDevice device = NetworkDevice.fromTag(deviceList.getCompoundTagAt(i));
            router.NETWORK_DEVICES.put(device.getUUID(), device);
        }
        return router;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(!(obj instanceof Router))
            return false;
        Router router = (Router) obj;
        return router.getId().equals(routerId);
    }
}
