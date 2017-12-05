package com.mrcrayfish.device.init;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.block.BlockLaptop;
import com.mrcrayfish.device.item.ItemLaptop;

import com.mrcrayfish.device.block.BlockPaper;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.item.ItemPaper;
import com.mrcrayfish.device.block.BlockPaper;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.item.ItemPaper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

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
		registerBlock(PRINTER);
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