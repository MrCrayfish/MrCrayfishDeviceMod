package com.mrcrayfish.device.init;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.block.BlockLaptop;

import com.mrcrayfish.device.block.BlockPaper;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.item.ItemPaper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DeviceBlocks 
{
	public static final Block LAPTOP;
	public static final Block PRINTER;
	public static final Block PAPER;
	static
	{
		LAPTOP = new BlockLaptop().setUnlocalizedName("laptop").setRegistryName("laptop");
		PRINTER = new BlockPrinter().setUnlocalizedName("printer").setRegistryName("printer");
		PAPER = new BlockPaper();
	}

	public static void register()
	{
		registerBlock(LAPTOP);
		registerBlock(PRINTER);
		registerBlock(PAPER, new ItemPaper(PAPER));
	}

	public static void registerBlock(Block block)
	{
		registerBlock(block, new ItemBlock(block));
	}

	private static void registerBlock(Block block, ItemBlock item)
	{
		GameRegistry.register(block);
		item.setRegistryName(block.getRegistryName());
		GameRegistry.register(item);
	}

	public static void registerRenders() 
	{
		registerRender(LAPTOP);
		registerRender(PRINTER);
		registerRender(PAPER);
	}
	
	private static void registerRender(Block blockIn)
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockIn), 0, new ModelResourceLocation(Reference.MOD_ID + ":" + blockIn.getUnlocalizedName().substring(5), "inventory"));
	}
}