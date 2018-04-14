package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class TextModule extends Module
{
    @Override
    public String[] getRequiredData()
    {
        return new String[] { "text" };
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        List<String> lines = Laptop.fontRenderer.listFormattedStringToWidth(data.get("text"), width);
        return lines.size() * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + lines.size() - 1;
    }

    @Override
    public void generate(Layout layout, Map<String, String> data, int width)
    {
        Text text = new Text(data.get("text"), 0, 0, width);
        layout.addComponent(text);
    }

    /*

    #text
    text=fadfdsfsdfsdfsdsdfdsfsdfsdfsdfsdf
    #wiki-item
    title=
    text=
    image=





     */
}
