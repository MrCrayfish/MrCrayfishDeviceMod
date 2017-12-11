package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.tileentity.TileEntityDevice;
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
public class Connection
{
    private UUID routerId;
    private BlockPos routerPos;

    private Connection() {}

    public Connection(Router router)
    {
        this.routerId = router.getId();
        this.routerPos = router.getPos();
    }

    public UUID getRouterId()
    {
        return routerId;
    }

    @Nullable
    public BlockPos getRouterPos()
    {
        return routerPos;
    }

    public void setRouterPos(BlockPos routerPos)
    {
        this.routerPos = routerPos;
    }

    @Nullable
    public Router getRouter(World world)
    {
        if(routerPos == null)
            return null;

        TileEntity tileEntity = world.getTileEntity(routerPos);
        if(tileEntity instanceof TileEntityRouter)
        {
            TileEntityRouter router = (TileEntityRouter) tileEntity;
            if(router.getRouter().getId().equals(routerId))
            {
                return router.getRouter();
            }
        }
        return null;
    }

    public boolean isConnected(TileEntityDevice device, World world)
    {
        if(routerPos == null)
            return false;

        TileEntity tileEntity = world.getTileEntity(routerPos);
        if(tileEntity instanceof TileEntityRouter)
        {
            TileEntityRouter tileEntityRouter = (TileEntityRouter) tileEntity;
            Router router = tileEntityRouter.getRouter();
            return router.hasDevice(device);
        }
        return false;
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", routerId.toString());
        return tag;
    }

    public static Connection fromTag(TileEntityDevice device, NBTTagCompound tag)
    {
        Connection connection = new Connection();
        connection.routerId = UUID.fromString(tag.getString("id"));
        return connection;
    }
}
