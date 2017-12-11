package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.tileentity.TileEntityDevice;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class Router
{
    private final Map<UUID, NetworkDevice> NETWORK_DEVICES = new HashMap<>();

    private int timer;
    private UUID routerId;
    private BlockPos pos;

    public Router(BlockPos pos)
    {
        this.pos = pos;
    }

    public void update(World world)
    {
        if(++timer >= DeviceConfig.getBeaconInterval())
        {
            sendBeacon(world);
            timer = 0;
        }
    }

    public boolean addDevice(TileEntityDevice device)
    {
        if(NETWORK_DEVICES.size() >= DeviceConfig.getMaxDevices())
        {
            return NETWORK_DEVICES.containsKey(device.getId());
        }
        if(!NETWORK_DEVICES.containsKey(device.getId()))
        {
            NETWORK_DEVICES.put(device.getId(), new NetworkDevice(device, this));
        }
        return true;
    }

    public boolean hasDevice(TileEntityDevice device)
    {
        return NETWORK_DEVICES.containsKey(device.getId());
    }

    public void removeDevice(TileEntityDevice device)
    {
        NETWORK_DEVICES.remove(device.getId());
    }

    @Nullable
    public TileEntityDevice getDevice(World world, UUID id)
    {
        return NETWORK_DEVICES.containsKey(id) ? NETWORK_DEVICES.get(id).getDevice(world) : null;
    }

    public Collection<NetworkDevice> getNetworkDevices()
    {
        return NETWORK_DEVICES.values();
    }

    public Collection<NetworkDevice> getConnectedDevices(World world)
    {
        sendBeacon(world);
        return NETWORK_DEVICES.values().stream().filter(networkDevice -> networkDevice.getPos() != null).collect(Collectors.toList());
    }

    public Collection<NetworkDevice> getConnectedDevices(final World world, Class<? extends TileEntityDevice> type)
    {
        final Predicate<NetworkDevice> DEVICE_TYPE = networkDevice ->
        {
            if(networkDevice.getPos() == null)
                return false;

            TileEntity tileEntity = world.getTileEntity(networkDevice.getPos());
            if(tileEntity instanceof TileEntityDevice)
            {
                return tileEntity.getClass().isAssignableFrom(type);
            }
            return false;
        };
        return getConnectedDevices(world).stream().filter(DEVICE_TYPE).collect(Collectors.toList());
    }

    private void sendBeacon(World world)
    {
        if(world.isRemote)
            return;

        NETWORK_DEVICES.forEach((id, device) -> device.setPos(null));
        int range = DeviceConfig.getSignalRange();
        for(int y = -range; y < range + 1; y++)
        {
            for(int z = -range; z < range + 1; z++)
            {
                for(int x = -range; x < range + 1; x++)
                {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    TileEntity tileEntity = world.getTileEntity(currentPos);
                    if(tileEntity instanceof TileEntityDevice)
                    {
                        TileEntityDevice tileEntityDevice = (TileEntityDevice) tileEntity;
                        if(!NETWORK_DEVICES.containsKey(tileEntityDevice.getId()))
                            continue;
                        if(tileEntityDevice.receiveBeacon(this))
                        {
                            NETWORK_DEVICES.get(tileEntityDevice.getId()).setPos(currentPos);
                        }
                        else
                        {
                            NETWORK_DEVICES.remove(tileEntityDevice.getId());
                        }
                    }
                }
            }
        }
    }

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

    public NBTTagCompound toTag(boolean includePos)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setUniqueId("id", getId());

        NBTTagList deviceList = new NBTTagList();
        NETWORK_DEVICES.forEach((id, device) -> {
            deviceList.appendTag(device.toTag(includePos));
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
            router.NETWORK_DEVICES.put(device.getId(), device);
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
