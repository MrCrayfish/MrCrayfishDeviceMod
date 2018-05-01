package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.programs.gitweb.component.ContainerBox;
import com.mrcrayfish.device.programs.gitweb.component.CraftingBox;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public abstract class ContainerModule extends Module
{
    @Override
    public String[] getRequiredData()
    {
        return new String[0];
    }

    @Override
    public final int calculateHeight(Map<String, String> data, int width)
    {
        int height = getHeight() + 22;
        if(data.containsKey("desc"))
        {
            Text text = new Text(data.get("desc"), 0, data.containsKey("title") ? 12 : 5, width - CraftingBox.WIDTH - 5);
            text.setPadding(5);
            height += Math.max(0, (text.getHeight() + text.top) - height);
        }
        return height;
    }

    @Override
    public final void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data)
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

        ContainerBox box = createContainer(data);
        box.left = craftingX;
        box.top = craftingY;
        layout.addComponent(box);
    }

    public abstract int getHeight();

    public abstract ContainerBox createContainer(Map<String, String> data);

    protected ItemStack getItem(Map<String, String> data, String key)
    {
        if(data.containsKey(key))
        {
            try
            {
                return new ItemStack(JsonToNBT.getTagFromJson(data.get(key)));
            }
            catch(NBTException e)
            {
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }
}
