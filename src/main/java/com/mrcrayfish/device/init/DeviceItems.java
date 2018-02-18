package com.mrcrayfish.device.init;

import com.mrcrayfish.device.item.*;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class DeviceItems
{
    public static final Item FLASH_DRIVE;
    public static final Item ETHERNET_CABLE;

    public static final Item PLASTIC_UNREFINED;
    public static final Item PLASTIC;
    public static final Item PLASTIC_FRAME;

    public static final Item WHEEL;

    public static final Item COMPONENT_CIRCUIT_BOARD;
    public static final Item COMPONENT_MOTHERBOARD;
    public static final Item COMPONENT_CPU;
    public static final Item COMPONENT_RAM;
    public static final Item COMPONENT_GPU;
    public static final Item COMPONENT_WIFI;
    public static final Item COMPONENT_HARD_DRIVE;
    public static final Item COMPONENT_FLASH_CHIP;
    public static final Item COMPONENT_SOLID_STATE_DRIVE;
    public static final Item COMPONENT_BATTERY;
    public static final Item COMPONENT_SCREEN;

    static
    {
        FLASH_DRIVE = new ItemFlashDrive();
        ETHERNET_CABLE = new ItemEthernetCable();

        PLASTIC_UNREFINED = new ItemBasic("plastic_unrefined");
        PLASTIC = new ItemBasic("plastic");
        PLASTIC_FRAME = new ItemBasic("plastic_frame");

        WHEEL = new ItemBasic("wheel");

        COMPONENT_CIRCUIT_BOARD = new ItemComponent("circuit_board");
        COMPONENT_MOTHERBOARD = new ItemMotherboard();
        COMPONENT_CPU = new ItemMotherboard.Component("cpu");
        COMPONENT_RAM = new ItemMotherboard.Component("ram");
        COMPONENT_GPU = new ItemMotherboard.Component("gpu");
        COMPONENT_WIFI = new ItemMotherboard.Component("wifi");
        COMPONENT_HARD_DRIVE = new ItemComponent("hard_drive");
        COMPONENT_FLASH_CHIP = new ItemComponent("flash_chip");
        COMPONENT_SOLID_STATE_DRIVE = new ItemComponent("solid_state_drive");
        COMPONENT_BATTERY = new ItemComponent("battery");
        COMPONENT_SCREEN = new ItemComponent("screen");
    }

    public static void register()
    {
        register(FLASH_DRIVE);
        register(ETHERNET_CABLE);
        register(PLASTIC_UNREFINED);
        register(PLASTIC);
        register(PLASTIC_FRAME);
        register(WHEEL);
        register(COMPONENT_CIRCUIT_BOARD);
        register(COMPONENT_MOTHERBOARD);
        register(COMPONENT_CPU);
        register(COMPONENT_RAM);
        register(COMPONENT_GPU);
        register(COMPONENT_WIFI);
        register(COMPONENT_HARD_DRIVE);
        register(COMPONENT_FLASH_CHIP);
        register(COMPONENT_SOLID_STATE_DRIVE);
        register(COMPONENT_BATTERY);
        register(COMPONENT_SCREEN);
    }

    private static void register(Item item)
    {
        RegistrationHandler.Items.add(item);
    }
}
