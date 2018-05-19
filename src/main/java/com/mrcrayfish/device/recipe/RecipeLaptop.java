package com.mrcrayfish.device.recipe;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.init.DeviceItems;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class RecipeLaptop extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    public RecipeLaptop()
    {
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "laptop"));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        if(inv.getStackInSlot(0).isEmpty() || inv.getStackInSlot(0).getItem() != DeviceItems.PLASTIC_FRAME)
            return false;

        if(inv.getStackInSlot(1).isEmpty() || inv.getStackInSlot(1).getItem() != DeviceItems.COMPONENT_SCREEN)
            return false;

        if(inv.getStackInSlot(2).isEmpty() || inv.getStackInSlot(2).getItem() != DeviceItems.PLASTIC_FRAME)
            return false;

        if(inv.getStackInSlot(3).isEmpty() || inv.getStackInSlot(3).getItem() != DeviceItems.COMPONENT_BATTERY)
            return false;

        if(inv.getStackInSlot(4).isEmpty() || inv.getStackInSlot(4).getItem() != DeviceItems.COMPONENT_MOTHERBOARD)
            return false;

        if(inv.getStackInSlot(5).isEmpty() || inv.getStackInSlot(5).getItem() != DeviceItems.COMPONENT_HARD_DRIVE)
            return false;

        if(inv.getStackInSlot(6).isEmpty() || inv.getStackInSlot(6).getItem() != DeviceItems.PLASTIC_FRAME)
            return false;

        if(inv.getStackInSlot(7).isEmpty() || inv.getStackInSlot(7).getItem() != Items.DYE)
            return false;

        if(inv.getStackInSlot(8).isEmpty() || inv.getStackInSlot(8).getItem() != DeviceItems.PLASTIC_FRAME)
            return false;

        ItemStack motherboard = inv.getStackInSlot(4);
        NBTTagCompound tag = motherboard.getTagCompound();
        if(tag != null)
        {
            NBTTagCompound components = tag.getCompoundTag("components");
            return components.hasKey("cpu") && components.hasKey("ram") && components.hasKey("gpu") && components.hasKey("wifi");
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack dye = inv.getStackInSlot(7);
        return new ItemStack(DeviceBlocks.LAPTOP, 1, 15 - dye.getMetadata());
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return width >= 3 && height >= 3;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return null;
    }
}
