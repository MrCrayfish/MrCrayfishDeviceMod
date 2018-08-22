package com.mrcrayfish.device.util;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Author: MrCrayfish
 */
public class GLHelper
{
	private static Stack<Scissor> scissorStack = new Stack<>();
	private static final FloatBuffer PIXEL_RGB = BufferUtils.createFloatBuffer(3);

	public static void pushScissor(int x, int y, int width, int height)
	{
		if (scissorStack.size() > 0)
		{
			Scissor scissor = scissorStack.peek();
			x = Math.max(scissor.x, x);
			y = Math.max(scissor.y, y);
			width = x + width > scissor.x + scissor.width ? scissor.x + scissor.width - x : width;
			height = y + height > scissor.y + scissor.height ? scissor.y + scissor.height - y : height;
		}

		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution resolution = new ScaledResolution(mc);
		int scale = resolution.getScaleFactor();
		GL11.glScissor(x * scale, mc.displayHeight - y * scale - height * scale, Math.max(0, width * scale), Math.max(0, height * scale));
		scissorStack.push(new Scissor(x, y, width, height));
	}

	public static void popScissor()
	{
		if (!scissorStack.isEmpty())
		{
			scissorStack.pop();
		}
		restoreScissor();
	}

	private static void restoreScissor()
	{
		if (!scissorStack.isEmpty())
		{
			Scissor scissor = scissorStack.peek();
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution resolution = new ScaledResolution(mc);
			int scale = resolution.getScaleFactor();
			GL11.glScissor(scissor.x * scale, mc.displayHeight - scissor.y * scale - scissor.height * scale, Math.max(0, scissor.width * scale), Math.max(0, scissor.height * scale));
		}
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
		PIXEL_RGB.clear();
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution resolution = new ScaledResolution(mc);
		int scale = resolution.getScaleFactor();
		GL11.glReadPixels(x * scale, mc.displayHeight - y * scale - scale, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, PIXEL_RGB);
		return new Color(PIXEL_RGB.get(0), PIXEL_RGB.get(1), PIXEL_RGB.get(2));
	}

	private static class Scissor
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
