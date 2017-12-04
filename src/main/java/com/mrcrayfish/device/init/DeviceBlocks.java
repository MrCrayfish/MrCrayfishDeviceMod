package com.mrcrayfish.device.init;

import com.mrcrayfish.device.block.BlockLaptop;

import com.mrcrayfish.device.block.BlockRouter;
import com.mrcrayfish.device.item.ItemLaptop;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;


public class DeviceBlocks 
{
	public static final Block LAPTOP;
	public static final Block ROUTER;
    static
	{
		LAPTOP = new BlockLaptop();
        ROUTER = new BlockRouter();
	}

	public static void register()
	{
		registerBlock(LAPTOP, new ItemLaptop(LAPTOP));
		registerBlock(ROUTER);
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