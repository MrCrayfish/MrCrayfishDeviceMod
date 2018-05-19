package com.mrcrayfish.device.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

/**
 * Author: MrCrayfish
 */
@JEIPlugin
public class CraftingPlugin implements IModPlugin
{
    @Override
    public void register(IModRegistry registry)
    {
        registry.addRecipes(LaptopRecipeMaker.getLaptopRecipes(), VanillaRecipeCategoryUid.CRAFTING);
    }
}
