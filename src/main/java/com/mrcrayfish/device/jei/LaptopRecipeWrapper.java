package com.mrcrayfish.device.jei;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.recipe.RecipeLaptop;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class LaptopRecipeWrapper implements IShapedCraftingRecipeWrapper
{
    private List<ItemStack> inputs;
    private List<ItemStack> outputs;

    public LaptopRecipeWrapper(EnumDyeColor color)
    {
        ItemStack motherboard = new ItemStack(DeviceItems.COMPONENT_MOTHERBOARD);
        NBTTagCompound tag = motherboard.getOrCreateSubCompound("components");
        tag.setBoolean("cpu", true);
        tag.setBoolean("ram", true);
        tag.setBoolean("gpu", true);
        tag.setBoolean("wifi", true);

        ImmutableList.Builder<ItemStack> input = ImmutableList.builder();
        input.add(new ItemStack(DeviceItems.PLASTIC_FRAME));
        input.add(new ItemStack(DeviceItems.COMPONENT_SCREEN));
        input.add(new ItemStack(DeviceItems.PLASTIC_FRAME));
        input.add(new ItemStack(DeviceItems.COMPONENT_BATTERY));
        input.add(motherboard);
        input.add(new ItemStack(DeviceItems.COMPONENT_HARD_DRIVE));
        input.add(new ItemStack(DeviceItems.PLASTIC_FRAME));
        input.add(new ItemStack(Items.DYE, 1, color.getDyeDamage()));
        input.add(new ItemStack(DeviceItems.PLASTIC_FRAME));
        this.inputs = input.build();

        ImmutableList.Builder<ItemStack> output = ImmutableList.builder();
        output.add(new ItemStack(DeviceBlocks.LAPTOP, 1, color.getMetadata()));
        this.outputs = output.build();
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutputs(ItemStack.class, outputs);
    }

    @Override
    public int getWidth()
    {
        return 3;
    }

    @Override
    public int getHeight()
    {
        return 3;
    }
}
