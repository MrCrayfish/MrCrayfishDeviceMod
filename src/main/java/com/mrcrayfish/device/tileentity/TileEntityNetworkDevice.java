package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.core.network.Connection;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.util.IColored;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class TileEntityNetworkDevice extends TileEntityDevice implements ITickable
{
    private int counter;
    private Connection connection;

    @Override
    public void update()
    {
        if(world.isRemote)
            return;

        if(connection != null)
        {
            if(++counter >= DeviceConfig.getBeaconInterval() * 2)
            {
                connection.setRouterPos(null);
                counter = 0;
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

    public boolean isConnected()
    {
        return connection != null && connection.isConnected();
    }

    public boolean receiveBeacon(Router router)
    {
        if(counter >= DeviceConfig.getBeaconInterval() * 2)
        {
            connect(router);
            return true;
        }
        if(connection != null && connection.getRouterId().equals(router.getId()))
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

    @Nullable
    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentString(getCustomName());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
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
        if(compound.hasKey("connection", Constants.NBT.TAG_COMPOUND))
        {
            connection = Connection.fromTag(this, compound.getCompoundTag("connection"));
        }
    }

    public static abstract class Colored extends TileEntityNetworkDevice implements IColored
    {
        private EnumDyeColor color = EnumDyeColor.RED;

        @Override
        public void readFromNBT(NBTTagCompound compound)
        {
            super.readFromNBT(compound);
            if(compound.hasKey("color", Constants.NBT.TAG_BYTE))
            {
                this.color = EnumDyeColor.byMetadata(compound.getByte("color"));
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound)
        {
            super.writeToNBT(compound);
            compound.setByte("color", (byte) color.getMetadata());
            return compound;
        }

        @Override
        public NBTTagCompound writeSyncTag()
        {
            NBTTagCompound tag = super.writeSyncTag();
            tag.setByte("color", (byte) color.getMetadata());
            return tag;
        }

        @Override
        public final void setColor(EnumDyeColor color)
        {
            this.color = color;
        }

        @Override
        public final EnumDyeColor getColor()
        {
            return color;
        }
    }
}
