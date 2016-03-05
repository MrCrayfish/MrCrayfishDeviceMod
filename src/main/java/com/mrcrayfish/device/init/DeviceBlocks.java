package com.mrcrayfish.device.init;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.block.BlockLaptop;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DeviceBlocks 
{
	public static Block laptop;
	
	public static void init()
	{
		laptop = new BlockLaptop().setUnlocalizedName("laptop");
	}
	
	public static void register()
	{
		GameRegistry.registerBlock(laptop, laptop.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() 
	{
		registerRender(laptop);
	}
	
	private static void registerRender(Block blockIn)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(blockIn), 0, new ModelResourceLocation(Reference.MOD_ID + ":" + blockIn.getUnlocalizedName().substring(5), "inventory"));
	}
}