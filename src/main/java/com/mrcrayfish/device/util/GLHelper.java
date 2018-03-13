package com.mrcrayfish.device.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Stack;

/**
 * Author: MrCrayfish
 */
public class GLHelper
{
    public static Stack<Scissor> scissorStack = new Stack<>();

    public static void pushScissor(int x, int y, int width, int height)
    {
        if(scissorStack.size() > 0)
        {
            Scissor scissor = scissorStack.peek();
            x = Math.max(scissor.x, x);
            y = Math.max(scissor.y, y);
            width = x + width > scissor.x + scissor.width ? scissor.x + scissor.width - x : width;
            height = y + height > scissor.y + scissor.height ? scissor.y + scissor.height - y : height;
            if(x < 0 || y < 0 || width < 0 || height < 0)
            {
                System.out.println(x + " " + y + " " + width + " " + height);
                int t = 0;
            }
        }

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);
        int scale = resolution.getScaleFactor();
        GL11.glScissor(x * scale, mc.displayHeight - y * scale - height * scale, width * scale, height * scale);
        scissorStack.push(new Scissor(x, y, width, height));
    }

    public static void popScissor()
    {
        scissorStack.pop();
    }

    public static boolean isScissorStackEmpty()
    {
        return scissorStack.isEmpty();
    }

    /**
     * Do not call! Used for core only.
     */
    public static void clearScissorStack()
    {
        scissorStack.clear();
    }

    public static Color getPixel(int x, int y)
    {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);
        int scale = resolution.getScaleFactor();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(x * scale, mc.displayHeight - y * scale - scale, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        return new Color(buffer.get(0), buffer.get(1), buffer.get(2));
    }

    public static class Scissor
    {
        public int x;
        public int y;
        public int width;
        public int height;

        Scissor(int x, int y, int width, int height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
