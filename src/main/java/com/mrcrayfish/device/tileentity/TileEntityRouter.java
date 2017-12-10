package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.network.Router;
import net.minecraft.nbt.NBTTagCompound;
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
            router = new Router(pos);
        }
        return router;
    }

    @Override
    public void update()
    {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("router", getRouter().toTag());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        router = Router.fromTag(pos, compound.getCompoundTag("router"));
    }
}
