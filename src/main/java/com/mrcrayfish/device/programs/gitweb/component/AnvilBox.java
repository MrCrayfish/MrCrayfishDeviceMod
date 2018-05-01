package com.mrcrayfish.device.programs.gitweb.component;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class AnvilBox extends ContainerBox
{
    public static final int HEIGHT = 32;

    public AnvilBox(ItemStack source, ItemStack addition, ItemStack result)
    {
        super(0, 0, 0, 209, HEIGHT, new ItemStack(Blocks.ANVIL), "Anvil");
        slots.add(new Slot(12, 8, source));
        slots.add(new Slot(51, 8, addition));
        slots.add(new Slot(99, 8, result));
    }
}
