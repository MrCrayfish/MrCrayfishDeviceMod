package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.programs.gitweb.component.ContainerBox;
import com.mrcrayfish.device.programs.gitweb.component.CraftingBox;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class CraftingModule extends Module
{
    @Override
    public String[] getRequiredData()
    {
        return new String[0];
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        int height = CraftingBox.HEIGHT + 22;
        if(data.containsKey("desc"))
        {
            Text text = new Text(data.get("desc"), 0, data.containsKey("title") ? 12 : 5, width - CraftingBox.WIDTH - 5);
            text.setPadding(5);
            height += Math.max(0, (text.getHeight() + text.top) - height);
        }
        return height;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data)
    {
        int craftingX = (width - ContainerBox.WIDTH) / 2;
        int craftingY = 5;

        if(data.containsKey("title") || data.containsKey("desc"))
        {
            if(data.containsKey("title"))
            {
                Label label = new Label(TextFormatting.BOLD + data.get("title"), 5, 5);
                layout.addComponent(label);
            }
            if(data.containsKey("desc"))
            {
                Text text = new Text(data.get("desc"), 0, data.containsKey("title") ? 12 : 5, width - ContainerBox.WIDTH - 5);
                text.setPadding(5);
                layout.addComponent(text);
            }
            craftingX = width - ContainerBox.WIDTH - 5;
        }

        ItemStack[] ingredient = new ItemStack[9];
        Arrays.fill(ingredient, ItemStack.EMPTY);
        for(int i = 0; i < ingredient.length; i++)
        {
            if(data.containsKey("slot-" + (i + 1)))
            {
                try
                {
                    ingredient[i] = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-" + (i + 1))));
                }
                catch(NBTException e)
                {
                    e.printStackTrace();
                }
            }
        }

        ItemStack result = ItemStack.EMPTY;
        if(data.containsKey("slot-result"))
        {
            try
            {
                result = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-result")));
            }
            catch(NBTException e)
            {
                e.printStackTrace();
            }
        }

        layout.addComponent(new CraftingBox(craftingX, craftingY, ingredient, result));
    }
}
