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
        if(c == '\t') return 20;
        return super.getCharWidth(c);
    }

    @Override
    protected float renderUnicodeChar(char c, boolean italic)
    {
        if(!debug && c == '\n') return 0F;
        if(c == '\t')
        {
            if(debug) super.renderUnicodeChar(c, italic);
            return 20F;
        }
        return super.renderUnicodeChar(c, italic);
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }
}
