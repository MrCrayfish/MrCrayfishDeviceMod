package com.mrcrayfish.device.init;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.item.ItemFlashDrive;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class DeviceItems
{
    public static final Item FLASH_DRIVE;

    static
    {
        FLASH_DRIVE = new ItemFlashDrive();
    }

    public static void register()
    {
        register(FLASH_DRIVE);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
