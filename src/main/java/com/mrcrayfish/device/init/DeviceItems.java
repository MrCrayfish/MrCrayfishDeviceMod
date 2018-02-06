package com.mrcrayfish.device.init;

import com.mrcrayfish.device.item.ItemComponent;
import com.mrcrayfish.device.item.ItemEthernetCable;
import com.mrcrayfish.device.item.ItemFlashDrive;
import com.mrcrayfish.device.item.ItemMotherboard;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class DeviceItems
{
    public static final Item FLASH_DRIVE;
    public static final Item ETHERNET_CABLE;

    public static final Item COMPONENT_CIRCUIT_BOARD;
    public static final Item COMPONENT_MOTHERBOARD;
    public static final Item COMPONENT_CPU;
    public static final Item COMPONENT_RAM;
    public static final Item COMPONENT_GPU;
    public static final Item COMPONENT_WIFI;
    public static final Item COMPONENT_HARD_DRIVE;
    public static final Item COMPONENT_SCREEN;

    static
    {
        FLASH_DRIVE = new ItemFlashDrive();
        ETHERNET_CABLE = new ItemEthernetCable();
        COMPONENT_CIRCUIT_BOARD = new ItemComponent("circuit_board");
        COMPONENT_MOTHERBOARD = new ItemMotherboard();
        COMPONENT_CPU = new ItemMotherboard.Component("cpu");
        COMPONENT_RAM = new ItemMotherboard.Component("ram");
        COMPONENT_GPU = new ItemMotherboard.Component("gpu");
        COMPONENT_WIFI = new ItemComponent("wifi");
        COMPONENT_HARD_DRIVE = new ItemComponent("hard_drive");
        COMPONENT_SCREEN = new ItemComponent("screen");
    }

    public static void register()
    {
        register(FLASH_DRIVE);
        register(ETHERNET_CABLE);
        register(COMPONENT_CIRCUIT_BOARD);
        register(COMPONENT_MOTHERBOARD);
        register(COMPONENT_CPU);
        register(COMPONENT_RAM);
        register(COMPONENT_GPU);
        register(COMPONENT_WIFI);
        register(COMPONENT_HARD_DRIVE);
        register(COMPONENT_SCREEN);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
