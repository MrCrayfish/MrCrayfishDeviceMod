package com.mrcrayfish.device.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class LaptopFontRenderer extends FontRenderer
{
    public LaptopFontRenderer(Minecraft mc)
    {
        super(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), false);
        onResourceManagerReload(null);
    }

    @Override
    public int getCharWidth(char c)
    {
        if(c == '\n') return 0;
        return super.getCharWidth(c);
    }
}
