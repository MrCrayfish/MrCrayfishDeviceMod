package com.mrcrayfish.device.item;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ItemBasic extends Item
{
    public ItemBasic(String id)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(id);
        this.setCreativeTab(MrCrayfishDeviceMod.TAB_DEVICE);
    }
}
