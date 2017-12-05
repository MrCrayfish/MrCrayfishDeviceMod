package com.mrcrayfish.device.recipe;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.init.DeviceBlocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class RecipeCutPaper extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    public RecipeCutPaper()
    {
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "cut_paper"));
    }

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
                if(stack.getItem() == Item.getItemFromBlock(DeviceBlocks.PAPER))
                {
                    if(!paper.isEmpty()) return false;
                    paper = stack;
                }

                if(stack.getItem() == Items.SHEARS)
                {
                    if(!shear.isEmpty()) return false;
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
                if(stack.getItem() == Item.getItemFromBlock(DeviceBlocks.PAPER))
                {
                    if(!paper.isEmpty()) return ItemStack.EMPTY;
                    paper = stack;
                }
            }
        }

        if(!paper.isEmpty() && paper.hasTagCompound())
        {
            ItemStack result = new ItemStack(DeviceBlocks.PAPER);

            NBTTagCompound tag = paper.getTagCompound();
            if(!tag.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
            {
                return ItemStack.EMPTY;
            }

            NBTTagCompound blockTag = tag.getCompoundTag("BlockEntityTag");
            if(!blockTag.hasKey("print", Constants.NBT.TAG_COMPOUND))
            {
                return ItemStack.EMPTY;
            }

            NBTTagCompound printTag = blockTag.getCompoundTag("print");
            NBTTagCompound data = printTag.getCompoundTag("data");
            if(!data.hasKey("pixels", Constants.NBT.TAG_INT_ARRAY) || !data.hasKey("resolution", Constants.NBT.TAG_INT))
            {
                return ItemStack.EMPTY;
            }
            data.setBoolean("cut", true);
            result.setTagCompound(tag);

            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return width * height >= 2;
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

    @Override
    public boolean isDynamic()
    {
        return true;
    }
}
