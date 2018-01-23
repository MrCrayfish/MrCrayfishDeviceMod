package com.mrcrayfish.device.init;

import com.mrcrayfish.device.block.*;
import com.mrcrayfish.device.item.ItemColoredDevice;
import com.mrcrayfish.device.item.ItemPaper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;


public class DeviceBlocks 
{
	public static final Block LAPTOP;
    public static final Block ROUTER;
	public static final Block PRINTER;
	public static final Block PAPER;

	public static final Block OFFICE_CHAIR;

	static
	{
		LAPTOP = new BlockLaptop();
        ROUTER = new BlockRouter();
		PRINTER = new BlockPrinter();
		PAPER = new BlockPaper();

		OFFICE_CHAIR = new BlockOfficeChair();
	}

	public static void register()
	{
		registerBlock(LAPTOP, new ItemColoredDevice(LAPTOP));
        registerBlock(ROUTER, new ItemColoredDevice(ROUTER));
		registerBlock(PRINTER, new ItemColoredDevice(PRINTER));
		registerBlock(PAPER, new ItemPaper(PAPER));

		registerBlock(OFFICE_CHAIR, new ItemColoredDevice(OFFICE_CHAIR));
	}

	private static void registerBlock(Block block)
	{
		registerBlock(block, new ItemBlock(block));
	}

	private static void registerBlock(Block block, ItemBlock item)
	{
		if(block.getRegistryName() == null)
			throw new IllegalArgumentException("A block being registered does not have a registry name and could be successfully registered.");

		RegistrationHandler.Blocks.add(block);
		item.setRegistryName(block.getRegistryName());
		RegistrationHandler.Items.add(item);
	}
}