package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.util.Colorable;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

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
}
