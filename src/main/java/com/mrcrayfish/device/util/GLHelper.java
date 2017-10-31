package com.mrcrayfish.device.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class GLHelper
{
    public static void scissor(int x, int y, int width, int height)
    {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);
        int scale = resolution.getScaleFactor();
        GL11.glScissor(x * scale, mc.displayHeight - y * scale - height * scale, width * scale, height *scale);
    }
}
