package com.mrcrayfish.device.init;

import com.mrcrayfish.device.item.ItemComponent;
import com.mrcrayfish.device.item.ItemEthernetCable;
import com.mrcrayfish.device.item.ItemFlashDrive;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class DeviceItems
{
    public static final Item FLASH_DRIVE;
    public static final Item ETHERNET_CABLE;

    public static final Item COMPONENT_CPU;

    static
    {
        FLASH_DRIVE = new ItemFlashDrive();
        ETHERNET_CABLE = new ItemEthernetCable();
        COMPONENT_CPU = new ItemComponent("cpu");
    }

    public static void register()
    {
        register(FLASH_DRIVE);
        register(ETHERNET_CABLE);
        register(COMPONENT_CPU);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
