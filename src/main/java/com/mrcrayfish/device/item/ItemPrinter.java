package com.mrcrayfish.device.item;

import java.util.ArrayList;

import com.mrcrayfish.device.HasSubModels;
import com.mrcrayfish.device.Reference;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class ItemPrinter extends ItemBlock implements HasSubModels {

	public ItemPrinter(Block block)
    {
        super(block);
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

	@Override
	public ArrayList<ResourceLocation> getSubModels() {
		ArrayList<ResourceLocation> subModels = new ArrayList<ResourceLocation>();
		for(EnumDyeColor color : EnumDyeColor.values()) {
			subModels.add(new ResourceLocation(Reference.MOD_ID, "printer/" + color.getName()));
		}
		return subModels;
	}

}
