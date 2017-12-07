package com.mrcrayfish.device.item;

import java.util.ArrayList;

import com.mrcrayfish.device.HasSubModels;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ItemFlashDrive extends Item implements HasSubModels
{
    public ItemFlashDrive()
    {
        this.setUnlocalizedName("flash_drive");
        this.setRegistryName("flash_drive");
        this.setCreativeTab(MrCrayfishDeviceMod.tabDevice);
    }
    
    @Override
	public ArrayList<ResourceLocation> getSubModels() {
		ArrayList<ResourceLocation> subModels = new ArrayList<ResourceLocation>();
		for(EnumDyeColor color : EnumDyeColor.values()) {
			subModels.add(new ResourceLocation(Reference.MOD_ID, "flash_drive/" + color.getName()));
		}
		return subModels;
	}
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    	if(tab == MrCrayfishDeviceMod.tabDevice || tab == CreativeTabs.SEARCH) {
			for(EnumDyeColor color : EnumDyeColor.values()) {
				subItems.add(new ItemStack(this, 1, color.getMetadata()));
			}
		}
    }
}
