package com.mrcrayfish.device.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.mrcrayfish.device.HasSubModels;
import com.mrcrayfish.device.Reference;

/**
 * Author: MrCrayfish
 */
public class ItemLaptop extends ItemBlock implements HasSubModels
{
    public ItemLaptop(Block block)
    {
        super(block);
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean getShareTag()
    {
        return false;
    }

	@Override
	public ArrayList<ResourceLocation> getSubModels() {
		ArrayList<ResourceLocation> subModels = new ArrayList<ResourceLocation>();
		for(EnumDyeColor color : EnumDyeColor.values()) {
			subModels.add(new ResourceLocation(Reference.MOD_ID, "laptop/" + color.getName()));
		}
		return subModels;
	}
}
