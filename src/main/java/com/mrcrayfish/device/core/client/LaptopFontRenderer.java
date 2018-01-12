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
        switch(c)
        {
            case '\n': return 0;
            case '\t': return 20;
        }
        return super.getCharWidth(c);
    }

    @Override
    protected float renderUnicodeChar(char c, boolean italic)
    {
        if(debug && (c == '\n' || c == '\t'))
        {
            super.renderUnicodeChar(c, italic);
        }
        switch(c)
        {
            case '\n': return 0F;
            case '\t': return 20F;
        }
        return super.renderUnicodeChar(c, italic);
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }
}
