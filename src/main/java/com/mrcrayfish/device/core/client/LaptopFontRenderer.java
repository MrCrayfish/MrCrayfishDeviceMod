package com.mrcrayfish.device.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class LaptopFontRenderer extends FontRenderer
{
    private boolean debug = false;

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

    @Override
    protected float renderUnicodeChar(char ch, boolean italic)
    {
        if(ch == '\n' && !debug) return 0F;
        return super.renderUnicodeChar(ch, italic);
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }
}
