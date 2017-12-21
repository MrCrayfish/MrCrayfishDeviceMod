package com.mrcrayfish.device.init;

import com.mrcrayfish.device.item.ItemFlashDrive;

import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class DeviceItems {
	public static final Item FLASH_DRIVE;

	static {
		FLASH_DRIVE = new ItemFlashDrive();
	}

	public static void register() {
		register(FLASH_DRIVE);
	}

	private static void register(Item item) {
		RegistrationHandler.Items.add(item);
	}
}
