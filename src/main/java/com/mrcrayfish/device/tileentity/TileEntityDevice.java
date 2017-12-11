package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.core.network.Connection;
import com.mrcrayfish.device.core.network.IDevice;
import com.mrcrayfish.device.core.network.Router;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class TileEntityDevice extends TileEntitySync implements ITickable
{
    private int counter;
    private UUID deviceId;
    private Connection connection;

    @Override
    public void update()
    {
        if(world.isRemote)
            return;

        if(connection != null && connection.getRouterPos() != null)
        {
            if(++counter >= DeviceConfig.getBeaconInterval() * 2)
            {
                connection.setRouterPos(null);
            }
        }
    }

    public void connect(Router router)
    {
        if(router == null)
        {
            if(connection != null)
            {
                Router connectedRouter = connection.getRouter(world);
                if(connectedRouter != null)
                {
                    connectedRouter.removeDevice(this);
                }
            }
            connection = null;
            return;
        }
        connection = new Connection(router);
        counter = 0;
        this.markDirty();
    }

    public Connection getConnection()
    {
        return connection;
    }

    @Nullable
    public Router getRouter()
    {
        return connection != null ? connection.getRouter(world) : null;
    }

    public final UUID getId()
    {
        if(deviceId == null)
        {
            deviceId = UUID.randomUUID();
        }
        return deviceId;
    }

    public abstract String getDeviceName();

    public boolean isConnected()
    {
        return connection != null && connection.isConnected();
    }

    public boolean receiveBeacon(Router router)
    {
        if(connection.getRouterId().equals(router.getId()))
        {
            connection.setRouterPos(router.getPos());
            counter = 0;
            return true;
        }
        return false;
    }

    public int getSignalStrength()
    {
        BlockPos routerPos = connection.getRouterPos();
        if(routerPos != null)
        {
            double distance = Math.sqrt(pos.distanceSqToCenter(routerPos.getX() + 0.5, routerPos.getY() + 0.5, routerPos.getZ() + 0.5));
            double level = DeviceConfig.getSignalRange() / 3.0;
            return distance > level * 2 ? 2 : distance > level ? 1 : 0;
        }
        return -1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setString("deviceId", getId().toString());

        if(connection != null)
        {
            compound.setTag("connection", connection.toTag());
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("deviceId", Constants.NBT.TAG_STRING))
        {
            deviceId = UUID.fromString(compound.getString("deviceId"));
        }
        if(compound.hasKey("connection", Constants.NBT.TAG_COMPOUND))
        {
            connection = Connection.fromTag(this, compound.getCompoundTag("connection"));
        }
    }
}
