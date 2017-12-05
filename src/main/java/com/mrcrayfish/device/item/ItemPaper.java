package com.mrcrayfish.device.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ItemPaper extends ItemBlock
{
    public ItemPaper(Block block)
    {
        super(block);
        this.setMaxStackSize(1);
    }



    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if(tag != null)
        {
            NBTTagCompound copy = tag.copy();
            copy.removeTag("BlockEntityTag");
            return copy;
        }
        return null;
    }
}
