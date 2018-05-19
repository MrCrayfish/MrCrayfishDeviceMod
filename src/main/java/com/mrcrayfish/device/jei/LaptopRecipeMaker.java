package com.mrcrayfish.device.jei;

import net.minecraft.item.EnumDyeColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class LaptopRecipeMaker
{
    public static List<LaptopRecipeWrapper> getLaptopRecipes()
    {
        List<LaptopRecipeWrapper> recipes = new ArrayList<LaptopRecipeWrapper>();
        for(EnumDyeColor color : EnumDyeColor.values())
        {
            recipes.add(new LaptopRecipeWrapper(color));
        }
        return recipes;
    }

    public LaptopRecipeMaker() {}
}
