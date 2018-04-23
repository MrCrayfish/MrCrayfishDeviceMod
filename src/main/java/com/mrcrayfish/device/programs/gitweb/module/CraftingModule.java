package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.programs.gitweb.component.CraftingBox;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.text.TextFormatting;

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
        int height = 78;
        if(data.containsKey("title") && data.containsKey("desc"))
        {
            Text text = new Text(data.get("desc"), 0, 12, width - 130 - 5);
            text.setPadding(5);
            height += Math.max(0, (text.getHeight() + text.top) - height);
        }
        else if(data.containsKey("title"))
        {
            height += 10;
        }
        return height;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data)
    {
        int craftingX = (width - 130) / 2;
        int craftingY = 5;

        if(data.containsKey("title") && data.containsKey("desc"))
        {
            Label label = new Label(TextFormatting.BOLD + data.get("title"), 5, 5);
            layout.addComponent(label);

            Text text = new Text(data.get("desc"), 0, 12, width - 130 - 5);
            text.setPadding(5);
            layout.addComponent(text);
            craftingX = width - 130 - 5;
        }
        else if(data.containsKey("title"))
        {
            Label label = new Label(TextFormatting.BOLD + data.get("title"), width / 2, 5);
            label.setAlignment(Component.ALIGN_CENTER);
            layout.addComponent(label);
            craftingY += 10;
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
