package com.mrcrayfish.device.init;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.block.BlockLaptop;

import com.mrcrayfish.device.block.BlockRouter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DeviceBlocks 
{
	public static Block laptop;
	public static Block router;
	
	public static void init()
	{
		laptop = new BlockLaptop().setUnlocalizedName("laptop").setRegistryName("laptop");
		router = new BlockRouter();
	}
	
	public static void register()
	{
		registerBlock(laptop);
		registerBlock(router);
	}
	
	public static void registerBlock(Block block)
	{
		GameRegistry.register(block);
		ItemBlock item = new ItemBlock(block);
		item.setRegistryName(block.getRegistryName());
		GameRegistry.register(item);
	}

	@SideOnly(Side.CLIENT)
	public static void registerRenders() 
	{
		registerRender(laptop);
		registerRender(router);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block blockIn)
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockIn), 0, new ModelResourceLocation(Reference.MOD_ID + ":" + blockIn.getUnlocalizedName().substring(5), "inventory"));
	}
}