package com.mrcryafish.device.init;

import com.mrcryafish.device.Reference;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class DeviceBlocks 
{
	public static Block laptop;
	
	public static void init()
	{
		
	}
	
	public static void register()
	{
		
	}
	
	public static void registerRenders() 
	{
		
	}
	
	private static void registerRender(Block blockIn)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(blockIn), 0, new ModelResourceLocation(Reference.MOD_ID + ":" + blockIn.getUnlocalizedName().substring(5), "inventory"));
	}
}
