package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.network.Connection;
import com.mrcrayfish.device.core.network.Router;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class TileEntityDevice extends TileEntity implements ITickable
{
    private int counter;
    private UUID deviceId;
    private Connection connection;

    @Override
    public void update()
    {
        if(connection != null)
        {
            if(++counter >= 100)
            {
                connection.updateConnection(world);
                counter = 0;
            }
        }
    }

    public void connect(Router router)
    {
        if(router == null)
        {
            connection = null;
            return;
        }
        connection = new Connection(router.getId());
        connection.setDevicePos(pos);
        connection.updateConnection(world);
        this.markDirty();
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

    public boolean isConnected(World world)
    {
        return connection != null && connection.isActive(world);
    }

    public abstract String getDeviceName();

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setString("deviceId", getId().toString());
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
    }
}
