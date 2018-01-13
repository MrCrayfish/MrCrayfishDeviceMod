package com.mrcrayfish.device.init;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class RegistrationHandler
{
    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class Blocks
    {
        private static final List<Block> BLOCKS = new LinkedList<>();

        static void add(Block block)
        {
            BLOCKS.add(block);
        }

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Block> event)
        {
            MrCrayfishDeviceMod.getLogger().info("Registering blocks");
            BLOCKS.forEach(block -> event.getRegistry().register(block));
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class Items
    {
        private static final List<Item> ITEMS = new LinkedList<>();

        static void add(Item item)
        {
            ITEMS.add(item);
        }

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<Item> event)
        {
            MrCrayfishDeviceMod.getLogger().info("Registering items");
            ITEMS.forEach(item -> event.getRegistry().register(item));
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class Recipes
    {
        private static final List<IRecipe> RECIPES = new LinkedList<>();

        static void add(IRecipe recipe)
        {
            RECIPES.add(recipe);
        }

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<IRecipe> event)
        {
            MrCrayfishDeviceMod.getLogger().info("Registering recipes");
            RECIPES.forEach(recipe -> event.getRegistry().register(recipe));
            System.out.println(event.getRegistry().getEntries());
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
    public static class Models
    {
        @SubscribeEvent
        public static void register(ModelRegistryEvent event)
        {
            MrCrayfishDeviceMod.getLogger().info("Registering models");
            Items.ITEMS.forEach(item -> registerRender(item));
        }

        private static void registerRender(Item item)
        {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
        }
    }

    public static void init()
    {
        DeviceBlocks.register();
        DeviceItems.register();
        DeviceCrafting.register();
    }
}
