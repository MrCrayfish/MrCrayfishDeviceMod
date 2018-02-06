package com.mrcrayfish.device.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ItemMotherboard extends ItemComponent
{
    public ItemMotherboard()
    {
        super("motherboard");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if(!GuiScreen.isShiftKeyDown())
        {
            tooltip.add("CPU: " + getComponentStatus(tag, "cpu"));
            tooltip.add("RAM: " + getComponentStatus(tag, "ram"));
            tooltip.add("GPU: " + getComponentStatus(tag, "gpu"));
            tooltip.add(TextFormatting.YELLOW + "Hold shift for help");
        }
        else
        {
            tooltip.add("To add the required components");
            tooltip.add("place the motherboard and the");
            tooltip.add("corresponding component into a");
            tooltip.add("crafting table to combine them.");
        }
    }

    private String getComponentStatus(NBTTagCompound tag, String component)
    {
        if(tag != null && tag.hasKey("components", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound components = tag.getCompoundTag("components");
            if(components.hasKey(component, Constants.NBT.TAG_BYTE))
            {
                return TextFormatting.GREEN + "Added";
            }
        }
        return TextFormatting.RED + "Missing";
    }

    public static class Component extends ItemComponent
    {
        public Component(String id)
        {
            super(id);
        }
    }
}
