package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.entity.EntitySeat;
import com.mrcrayfish.device.util.Colorable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class TileEntityOfficeChair extends TileEntitySync implements Colorable
{
    private EnumDyeColor color = EnumDyeColor.RED;

    @Override
    public EnumDyeColor getColor()
    {
        return color;
    }

    @Override
    public void setColor(EnumDyeColor color)
    {
        this.color = color;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("color", Constants.NBT.TAG_BYTE))
        {
            color = EnumDyeColor.byMetadata(compound.getByte("color"));
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
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("color", (byte) color.getMetadata());
        return tag;
    }

    @SideOnly(Side.CLIENT)
    public float getRotation()
    {
        List<EntitySeat> seats = world.getEntitiesWithinAABB(EntitySeat.class, new AxisAlignedBB(pos));
        if(!seats.isEmpty())
        {
            EntitySeat seat = seats.get(0);
            if(seat.getRidingEntity() != null)
            {
                if(seat.getRidingEntity() instanceof EntityLivingBase)
                {
                    EntityLivingBase living = (EntityLivingBase) seat.getRidingEntity();
                    living.renderYawOffset = living.rotationYaw;
                    living.prevRenderYawOffset = living.rotationYaw;
                    return living.rotationYaw - 90F;
                }
                return seat.getRidingEntity().rotationYaw - 90F;
            }
        }
        return getBlockMetadata() * 90F + 90F;
    }
}
