package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.network.NetworkClient;
import com.mrcrayfish.device.core.network.Router;
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
public abstract class TileEntityNetworkDevice extends TileEntity
{
    private UUID deviceId;
    private BlockPos connectedRouter;

    public final UUID getUUID()
    {
        if(deviceId == null)
        {
            deviceId = UUID.randomUUID();
        }
        return deviceId;
    }

    @Nullable
    public Router getConnectedRouter(World world)
    {
        //return connectedRouter;
        return null;
    }

    public boolean isConnected(World world)
    {
        if(connectedRouter == null)
            return false;

        TileEntity tileEntity = world.getTileEntity(connectedRouter);
        if(tileEntity instanceof TileEntityRouter)
        {
            TileEntityRouter tileEntityRouter = (TileEntityRouter) tileEntity;
            Router router = tileEntityRouter.getRouter();
            //router.
        }
        return false;
    }

    public void connectTo(Router router)
    {
        if(connectedRouter != null)
        {
            //connectedRouter.forgetDevice();
        }
    }

    public abstract String getDeviceName();

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setString("deviceId", getUUID().toString());
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
