package com.mrcrayfish.device.init;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Author: MrCrayfish
 */
public class DeviceItems
{
    public static final Item FLASH_DRIVE;

    static
    {
        FLASH_DRIVE = new Item().setUnlocalizedName("flash_drive").setRegistryName("flash_drive").setCreativeTab(MrCrayfishDeviceMod.tabDevice);
    }

    public static void register()
    {
        GameRegistry.register(FLASH_DRIVE);
    }

    public static void registerRenders()
    {
        registerRender(FLASH_DRIVE);
    }

    private static void registerRender(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }
}
