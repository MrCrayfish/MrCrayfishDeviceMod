package com.mrcrayfish.device.item;

import com.mrcrayfish.device.init.DeviceBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLaptop extends ItemBlock {

	public ItemLaptop() {
		super(DeviceBlocks.laptop);
		this.setRegistryName(DeviceBlocks.laptop.getRegistryName());
		this.setUnlocalizedName(DeviceBlocks.laptop.getUnlocalizedName());
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		//ItemArmor
		IBlockState state = worldIn.getBlockState(pos);  
		Block b = state.getBlock();
		if(b instanceof BlockCauldron && stack.getItemDamage() != 0) {
			int level = state.getValue(BlockCauldron.LEVEL);
			if(level != 0) {
				((BlockCauldron)b).setWaterLevel(worldIn, pos, state, level - 1);
				stack.setItemDamage(0);
				playerIn.setHeldItem(hand, stack);
				return EnumActionResult.SUCCESS;
			}
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	
}
