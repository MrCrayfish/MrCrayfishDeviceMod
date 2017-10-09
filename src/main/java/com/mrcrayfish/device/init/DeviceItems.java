package com.mrcrayfish.device.init;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Author: MrCrayfish
 */
public class DeviceItems
{
    public static Item flash_drive;

    public static void init()
    {
        flash_drive = new Item().setUnlocalizedName("flash_drive").setRegistryName("flash_drive").setCreativeTab(MrCrayfishDeviceMod.tabDevice);
    }

    public static void register()
    {
        GameRegistry.register(flash_drive);
    }

    public static void registerRenders()
    {
        registerRender(flash_drive);
    }

    private static void registerRender(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }
}
