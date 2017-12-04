package com.mrcrayfish.device.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ItemLaptop extends ItemBlock
{
    public ItemLaptop(Block block)
    {
        super(block);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean getShareTag()
    {
        return false;
    }
}
