package com.mrcrayfish.device.init;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.block.BlockLaptop;

import com.mrcrayfish.device.item.ItemLaptop;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DeviceBlocks 
{
	public static Block laptop;
	
	public static void init()
	{
		laptop = new BlockLaptop().setUnlocalizedName("laptop").setRegistryName("laptop");
	}
	
	public static void register()
	{
		registerBlock(laptop, new ItemLaptop(laptop));
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
		registerRender(laptop);
	}
	
	private static void registerRender(Block blockIn)
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockIn), 0, new ModelResourceLocation(Reference.MOD_ID + ":" + blockIn.getUnlocalizedName().substring(5), "inventory"));
	}
}