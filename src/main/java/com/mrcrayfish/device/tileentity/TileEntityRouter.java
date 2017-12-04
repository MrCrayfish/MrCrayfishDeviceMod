package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.network.Router;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 * Author: MrCrayfish
 */
public class TileEntityRouter extends TileEntity implements ITickable
{
    private Router router;

    public Router getRouter()
    {
        if(router == null)
        {
            router = new Router();
        }
        return router;
    }

    @Override
    public void update()
    {

    }
}
