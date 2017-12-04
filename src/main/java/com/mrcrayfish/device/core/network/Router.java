package com.mrcrayfish.device.core.network;

import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class Router
{
    private static final Map<UUID, NetworkDevice> NETWORK_DEVICES = new HashMap<>();

    private UUID routerId;

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
        return tag;
    }

    public void fromTag()
    {

    }
}
