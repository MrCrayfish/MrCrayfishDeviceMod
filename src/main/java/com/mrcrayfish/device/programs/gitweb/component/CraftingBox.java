package com.mrcrayfish.device.programs.gitweb.component;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.rmi.activation.ActivationGroup_Stub;

/**
 * Author: MrCrayfish
 */
public class CraftingBox extends ContainerBox
{
    public static final int HEIGHT = 68;

    public CraftingBox(ItemStack[] ingredients, ItemStack result)
    {
        super(0, 0, 0, 0, HEIGHT, new ItemStack(Blocks.CRAFTING_TABLE), "Crafting Table");
        this.setIngredients(ingredients);
        this.slots.add(new Slot(102, 35, result));
    }

    private void setIngredients(ItemStack[] ingredients)
    {
        for(int i = 0; i < ingredients.length; i++)
        {
            int posX = (i % 3) * 18 + 8;
            int posY = (i / 3) * 18 + 19;
            slots.add(new Slot(posX, posY, ingredients[i]));
        }
    }
}
