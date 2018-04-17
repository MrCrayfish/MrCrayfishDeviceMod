package com.mrcrayfish.device.programs.gitweb.component;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class CraftingBox extends Component
{
    private static final ResourceLocation CRAFTING_BOX_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/crafting_box.png");

    private ItemStack[] ingredients;
    private ItemStack result;

    public CraftingBox(int left, int top, ItemStack[] ingredients, ItemStack result)
    {
        super(left, top);
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        mc.getTextureManager().bindTexture(CRAFTING_BOX_TEXTURE);
        RenderUtil.drawRectWithTexture(x, y, 0, 0, 130, 68, 130, 68, 256, 256);

        for(int i = 0; i < ingredients.length; i++)
        {
            int posX = x + (i % 3) * 18 + 8;
            int posY = y + (i / 3) * 18 + 8;
            RenderUtil.renderItem(posX, posY, ingredients[i], true);
        }
        RenderUtil.renderItem(x + 102, y + 26, result, true);

        if(GuiHelper.isMouseWithin(mouseX, mouseY, x + 7, y + 7, 54, 54))
        {
            int hoveredSlot = (mouseX - x - 8) / 18 + (mouseY - y - 8) / 18 * 3;
            ItemStack hoveredItem = ingredients[hoveredSlot];
            if(!hoveredItem.isEmpty())
            {
                net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(hoveredItem);
                laptop.drawHoveringText(laptop.getItemToolTip(hoveredItem), mouseX, mouseY);
                net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
            }
        }
        
        if(GuiHelper.isMouseWithin(mouseX, mouseY, x + 101, y + 25, 18, 18))
        {
            if(!result.isEmpty())
            {
                net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(result);
                laptop.drawHoveringText(laptop.getItemToolTip(result), mouseX, mouseY);
                net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
            }
        }
    }
}
