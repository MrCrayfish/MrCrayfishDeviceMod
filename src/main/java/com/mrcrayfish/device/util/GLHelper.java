package com.mrcrayfish.device.util;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Author: MrCrayfish
 */
public class GLHelper {

	public static void scissor(int x, int y, int width, int height)
	{
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution resolution = new ScaledResolution(mc);
		int scale = resolution.getScaleFactor();
		GL11.glScissor(x * scale, mc.displayHeight - y * scale - height * scale, width * scale, height * scale);
	}

	/**
	 * Gets the color of the pixel at the specified point
	 * 
	 * @param x
	 *            The x of the pixel
	 * @param y
	 *            The y of the pixel
	 * @return The color of the pixel at that point
	 */
	public static Color getPixel(int x, int y)
	{
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution resolution = new ScaledResolution(mc);
		int scale = resolution.getScaleFactor();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		GL11.glReadPixels(x * scale, mc.displayHeight - y * scale - scale, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
		return new Color(buffer.get(0), buffer.get(1), buffer.get(2));
	}

	/**
	 * Sets the currently bound color to the specified color
	 * 
	 * @param color
	 *            The new color to use
	 */
	public static void color(Color color)
	{
		color(color.getRGB());
	}

	/**
	 * Sets the currently bound color to the specified color
	 * 
	 * @param color
	 *            The new color to use
	 */
	public static void color(int color)
	{
		GlStateManager.color((float) ((color >> 16) & 0xFF) / 255, (float) ((color >> 8) & 0xFF) / 255, (float) ((color >> 0) & 0xFF) / 255, (float) ((color >> 24) & 0xFF) / 255);
	}
}
