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
    public static Item paper_printed;

    public static void init()
    {
        flash_drive = new Item().setUnlocalizedName("flash_drive").setRegistryName("flash_drive").setCreativeTab(MrCrayfishDeviceMod.tabDevice);
        paper_printed = new Item().setUnlocalizedName("paper_printed").setRegistryName("paper_printed");
    }

    public static void register()
    {
        GameRegistry.register(flash_drive);
        GameRegistry.register(paper_printed);
    }

    public static void registerRenders()
    {
        registerRender(flash_drive);
        registerRender(paper_printed);
    }

    private static void registerRender(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }
}
