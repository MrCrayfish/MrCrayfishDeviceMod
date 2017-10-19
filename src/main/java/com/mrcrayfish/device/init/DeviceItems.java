package com.mrcrayfish.device.init;

import java.util.HashMap;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.item.ItemLaptop;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Author: MrCrayfish
 */
public class DeviceItems
{
    public static Item flash_drive, laptop;

    public static void init()
    {
        flash_drive = new Item().setUnlocalizedName("flash_drive").setRegistryName("flash_drive").setCreativeTab(MrCrayfishDeviceMod.tabDevice);
        laptop = new ItemLaptop();
    }

    public static void register()
    {
        GameRegistry.register(flash_drive);
        GameRegistry.register(laptop);
    }

    public static void registerRenders()
    {
        registerRender(flash_drive);
        HashMap<Integer, ResourceLocation> laptopColors = new HashMap<Integer, ResourceLocation>();
        for(EnumDyeColor col : EnumDyeColor.values()) {
        	laptopColors.put(col.getMetadata(), new ResourceLocation(Reference.MOD_ID, "laptop/" + col.getName()));
        }
        registerRender(laptop, laptopColors);
    }

    private static void registerRender(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }
    
    private static void registerRender(Item item, HashMap<Integer, ResourceLocation> locs)
    {
    	for(int key : locs.keySet()) {
    		ModelLoader.setCustomModelResourceLocation(item, key, new ModelResourceLocation(locs.get(key), "inventory"));
    	}
    }
}
