package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.programs.gitweb.component.CraftingBox;
import com.mrcrayfish.device.programs.gitweb.component.FurnaceBox;
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
public class FurnaceModule extends Module
{
    @Override
    public String[] getRequiredData()
    {
        return new String[0];
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        int height = 89;
        if(data.containsKey("desc"))
        {
            Text text = new Text(data.get("desc"), 0, data.containsKey("title") ? 12 : 5, width - 128 - 5);
            text.setPadding(5);
            height += Math.max(0, (text.getHeight() + text.top) - height);
        }
        return height;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data)
    {
        int craftingX = (width - 128) / 2;
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
                Text text = new Text(data.get("desc"), 0, data.containsKey("title") ? 12 : 5, width - 128 - 5);
                text.setPadding(5);
                layout.addComponent(text);
            }
            craftingX = width - 128 - 5;
        }

        ItemStack input = ItemStack.EMPTY;
        if(data.containsKey("slot-input"))
        {
            try
            {
                input = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-input")));
            }
            catch(NBTException e)
            {
                e.printStackTrace();
            }
        }

        ItemStack fuel = ItemStack.EMPTY;
        if(data.containsKey("slot-fuel"))
        {
            try
            {
                fuel = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-fuel")));
            }
            catch(NBTException e)
            {
                e.printStackTrace();
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

        layout.addComponent(new FurnaceBox(craftingX, craftingY, input, fuel, result));
    }
}
