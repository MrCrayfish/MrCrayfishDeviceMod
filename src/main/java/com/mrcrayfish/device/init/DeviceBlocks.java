package com.mrcrayfish.device.init;

import com.mrcrayfish.device.block.BlockLaptop;
import com.mrcrayfish.device.block.BlockPaper;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.item.ItemLaptop;
import com.mrcrayfish.device.item.ItemPaper;
import com.mrcrayfish.device.item.ItemPrinter;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class DeviceBlocks 
{
	public static final Block LAPTOP;
	public static final Block PRINTER;
	public static final Block PAPER;

	static
	{
		LAPTOP = new BlockLaptop();
		PRINTER = new BlockPrinter();
		PAPER = new BlockPaper();
	}

	public static void register()
	{
		registerBlock(LAPTOP, new ItemLaptop(LAPTOP));
		registerBlock(PRINTER, new ItemPrinter(PRINTER));
		registerBlock(PAPER, new ItemPaper(PAPER));
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