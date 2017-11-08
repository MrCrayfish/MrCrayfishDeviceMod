package com.mrcrayfish.device.init;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DeviceCrafting
{
	public static void register()
	{
		GameRegistry.addRecipe(new IRecipe()
		{
			@Override
			public boolean matches(InventoryCrafting inv, World worldIn)
			{
				ItemStack paper = ItemStack.EMPTY;
				ItemStack shear = ItemStack.EMPTY;

				for(int i = 0; i < inv.getSizeInventory(); i++)
				{
					ItemStack stack = inv.getStackInSlot(i);
					if(!stack.isEmpty())
					{
						if(stack.getItem() == DeviceItems.paper_printed)
						{
							if(!paper.isEmpty())
								return false;
							paper = stack;
						}

						if(stack.getItem() == Items.SHEARS)
						{
							if(!shear.isEmpty())
								return false;
							shear = stack;
						}
					}
				}

				return !paper.isEmpty() && !shear.isEmpty();
			}

			@Override
			public ItemStack getCraftingResult(InventoryCrafting inv)
			{
				ItemStack paper = ItemStack.EMPTY;
				for(int i = 0; i < inv.getSizeInventory(); i++)
				{
					ItemStack stack = inv.getStackInSlot(i);
					if(!stack.isEmpty())
					{
						if(stack.getItem() == DeviceItems.paper_printed)
						{
							if(!paper.isEmpty())
								return ItemStack.EMPTY;
							paper = stack;
						}
					}
				}

				if(!paper.isEmpty() && paper.hasTagCompound())
				{
					ItemStack result = new ItemStack(DeviceItems.paper_printed);
					NBTTagCompound tag = paper.getTagCompound().copy();
					if(!tag.hasKey("pixels", Constants.NBT.TAG_INT_ARRAY) || !tag.hasKey("resolution", Constants.NBT.TAG_INT))
					{
						return ItemStack.EMPTY;
					}
					tag.setBoolean("cut", true);
					result.setTagCompound(tag);
					return result;
				}

				return ItemStack.EMPTY;
			}

			@Override
			public int getRecipeSize()
			{
				return 4;
			}

			@Override
			public ItemStack getRecipeOutput()
			{
				return ItemStack.EMPTY;
			}

			@Override
			public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
			{
				NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
				for(int i = 0; i < inv.getSizeInventory(); i++)
				{
					ItemStack stack = inv.getStackInSlot(i);
					if(!stack.isEmpty() && stack.getItem() == Items.SHEARS)
					{
						ItemStack copy = stack.copy();
						copy.setCount(1);
						copy.setItemDamage(copy.getItemDamage() + 1);
						if(copy.getItemDamage() >= copy.getMaxDamage()) break;
						list.set(i, copy);
						break;
					}
				}
				return list;
			}
		});
	}
}
