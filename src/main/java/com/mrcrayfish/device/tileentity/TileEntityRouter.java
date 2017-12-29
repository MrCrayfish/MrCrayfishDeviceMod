package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.util.Colorable;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class TileEntityRouter extends TileEntitySync implements ITickable, Colorable
{
    private EnumDyeColor color = EnumDyeColor.RED;

    private Router router;

    @SideOnly(Side.CLIENT)
    private int debugTimer;

    public Router getRouter()
    {
        if(router == null)
        {
            router = new Router(pos);
            markDirty();
        }
        return router;
    }

    public void update()
    {
        if(!world.isRemote)
        {
            getRouter().update(world);
        }
        else if(debugTimer > 0)
        {
            debugTimer--;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isDebug()
    {
        return debugTimer > 0;
    }

    @SideOnly(Side.CLIENT)
    public void setDebug()
    {
        if(debugTimer <= 0)
        {
            debugTimer = 1200;
        }
        else
        {
            debugTimer = 0;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("router", getRouter().toTag(false));
        compound.setByte("color", (byte) color.getDyeDamage());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("router", Constants.NBT.TAG_COMPOUND))
        {
            router = Router.fromTag(pos, compound.getCompoundTag("router"));
        }
        if(compound.hasKey("color", Constants.NBT.TAG_BYTE))
        {
            this.color = EnumDyeColor.byDyeDamage(compound.getByte("color"));
        }
    }

    @Override
    public NBTTagCompound writeSyncTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("color", (byte) color.getDyeDamage());
        return tag;
    }

    public void syncDevicesToClient()
    {
        pipeline.setTag("router", getRouter().toTag(true));
        sync();
    }

    @Override
    public double getMaxRenderDistanceSquared()
    {
        return 16384;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    public void setColor(EnumDyeColor color)
    {
        this.color = color;
    }

    public EnumDyeColor getColor()
    {
        return color;
    }
}
