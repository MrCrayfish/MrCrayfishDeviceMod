package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.tileentity.TileEntityRouter;
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
public class NetworkClient
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

    public void connectToRouter(Router router)
    {
        /*if(connectedRouter != null && router)
        {
            connectedRouter.forgetDevice();
        }*/
    }

    @Nullable
    public Router getConnectedRouter(World world)
    {
        if(connectedRouter == null)
            return null;

        TileEntity tileEntity = world.getTileEntity(connectedRouter);
        if(tileEntity instanceof TileEntityRouter)
        {
            TileEntityRouter tileEntityRouter = (TileEntityRouter) tileEntity;
            return tileEntityRouter.getRouter();
        }
        return null;
    }

    public boolean isConnected(World world)
    {
        if(connectedRouter == null)
            return false;
        Router router = getConnectedRouter(world);
        return router != null && router.isClientConnected(this);
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", getUUID().toString());
        if(connectedRouter != null)
        {
            tag.setLong("connectedRouter", connectedRouter.toLong());
        }
        return tag;
    }

    public void fromTag(NBTTagCompound tag)
    {
        deviceId = UUID.fromString(tag.getString("id"));
        if(tag.hasKey("connectedRouter", Constants.NBT.TAG_LONG))
        {
            connectedRouter = BlockPos.fromLong(tag.getLong("connectedRouter"));
        }
    }
}
