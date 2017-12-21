package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.util.Colorable;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;

/**
 * Author: MrCrayfish
 */
public class TileEntityOfficeChair extends TileEntity implements Colorable
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
}
