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
public class Connection
{
    private UUID routerId;
    private BlockPos routerPos;
    private BlockPos devicePos;

    private Connection() {}

    public Connection(UUID routerId)
    {
        this.routerId = routerId;
    }

    public UUID getRouterId()
    {
        return routerId;
    }

    public BlockPos getRouterPos()
    {
        return routerPos;
    }

    public void setRouterPos(BlockPos routerPos)
    {
        this.routerPos = routerPos;
    }

    public void setDevicePos(BlockPos devicePos)
    {
        this.devicePos = devicePos;
    }

    @Nullable
    public Router getRouter(World world)
    {
        if(routerPos == null) return null;
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

    public void updateConnection(World world)
    {
        Router router = findRouter(world);
        if(router != null)
        {
            routerPos = router.getPos();
        }
        else
        {
            routerPos = null;
        }
    }

    @Nullable
    private Router findRouter(World world)
    {
        int range = 20;
        for(int y = -range; y < range + 1; y++)
        {
            for(int z = -range; z < range + 1; z++)
            {
                for(int x = -range; x < range + 1; x++)
                {
                    BlockPos currentPos = new BlockPos(devicePos.getX() + x, devicePos.getY() + y, devicePos.getZ() + z);
                    TileEntity tileEntity = world.getTileEntity(currentPos);
                    if(tileEntity instanceof TileEntityRouter)
                    {
                        TileEntityRouter tileEntityRouter = (TileEntityRouter) tileEntity;
                        Router router = tileEntityRouter.getRouter();
                        if(router.getId().equals(routerId))
                        {
                            return router;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean isActive(World world)
    {
        updateConnection(world);

        if(routerPos == null)
            return false;

        TileEntity tileEntity = world.getTileEntity(routerPos);
        if(tileEntity instanceof TileEntityRouter)
        {
            TileEntityRouter tileEntityRouter = (TileEntityRouter) tileEntity;
            Router router = tileEntityRouter.getRouter();
            return router.getId().equals(routerId);
        }
        return false;
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", routerId.toString());
        if(routerPos != null)
        {
            tag.setLong("routerPos", routerPos.toLong());
        }
        return tag;
    }

    public static Connection fromTag(NBTTagCompound tag)
    {
        Connection connection = new Connection();
        connection.routerId = UUID.fromString(tag.getString("id"));
        if(tag.hasKey("routerPos", Constants.NBT.TAG_LONG))
        {
            connection.routerPos = BlockPos.fromLong(tag.getLong("routerPos"));
        }
        return connection;
    }
}
