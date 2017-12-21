package com.mrcrayfish.device.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * Author: MrCrayfish
 */
public class ItemLaptop extends ItemBlock {
	public ItemLaptop(Block block) {
		super(block);
		setMaxStackSize(1);
	}

	@Override
	public boolean getShareTag() {
		return false;
	}
}
