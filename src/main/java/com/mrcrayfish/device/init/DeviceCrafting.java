package com.mrcrayfish.device.init;

import com.mrcrayfish.device.recipe.RecipeCutPaper;
import com.mrcrayfish.device.recipe.RecipeMotherboard;

public class DeviceCrafting
{
	public static void register()
	{
		RegistrationHandler.Recipes.add(new RecipeCutPaper());
		RegistrationHandler.Recipes.add(new RecipeMotherboard());
	}
}
