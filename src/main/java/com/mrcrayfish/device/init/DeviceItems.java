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

    public static final Item COMPONENT_MOTHERBOARD;
    public static final Item COMPONENT_CPU;
    public static final Item COMPONENT_HARD_DRIVE;
    public static final Item COMPONENT_RAM;
    public static final Item COMPONENT_GPU;

    static
    {
        FLASH_DRIVE = new ItemFlashDrive();
        ETHERNET_CABLE = new ItemEthernetCable();
        COMPONENT_MOTHERBOARD = new ItemComponent("motherboard");
        COMPONENT_CPU = new ItemComponent("cpu");
        COMPONENT_HARD_DRIVE = new ItemComponent("hard_drive");
        COMPONENT_RAM = new ItemComponent("ram");
        COMPONENT_GPU = new ItemComponent("gpu");
    }

    public static void register()
    {
        register(FLASH_DRIVE);
        register(ETHERNET_CABLE);
        register(COMPONENT_MOTHERBOARD);
        register(COMPONENT_CPU);
        register(COMPONENT_HARD_DRIVE);
        register(COMPONENT_RAM);
        register(COMPONENT_GPU);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
