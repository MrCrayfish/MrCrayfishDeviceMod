package com.mrcrayfish.device;

import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;

/**
 * Interface created to check if an item should have sub models
 * @author Dbrown55
 */
public interface HasSubModels {

	ArrayList<ResourceLocation> getSubModels();
	
}
