package com.mrcrayfish.device.init;

import com.mrcrayfish.device.recipe.RecipeCutPaper;

public class DeviceCrafting
{
	public static void register()
	{
		RegistrationHandler.Recipes.add(new RecipeCutPaper());
	}
}
