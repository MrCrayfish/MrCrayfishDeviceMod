package com.mrcrayfish.device.core.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public void connectDevice(NetworkClient device)
    {
        if(NETWORK_DEVICES.containsKey(device.getUUID()))
        {
            NETWORK_DEVICES.get(device.getUUID()).update(device);
            return;
        }
        NETWORK_DEVICES.put(device.getUUID(), new NetworkDevice(device));
    }

    public void forgetDevice(NetworkClient device)
    {
        NETWORK_DEVICES.remove(device.getUUID());
    }

    public boolean isClientConnected(NetworkClient device)
    {
        return NETWORK_DEVICES.containsKey(device.getUUID());
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", getId().toString());

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
        router.routerId = UUID.fromString(tag.getString("id"));

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
