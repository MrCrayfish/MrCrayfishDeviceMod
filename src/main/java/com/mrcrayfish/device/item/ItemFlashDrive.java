package com.mrcrayfish.device.item;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ItemFlashDrive extends Item
{
    public ItemFlashDrive()
    {
        this.setUnlocalizedName("flash_drive");
        this.setRegistryName("flash_drive");
        this.setCreativeTab(MrCrayfishDeviceMod.tabDevice);
    }
}
