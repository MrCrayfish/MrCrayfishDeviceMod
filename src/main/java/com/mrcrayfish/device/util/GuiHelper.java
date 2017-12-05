package com.mrcrayfish.device.util;

public class GuiHelper 
{
	public static boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2;
	}

	public static boolean isMouseWithin(int mouseX, int mouseY, int x, int y, int width, int height)
	{
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
}
