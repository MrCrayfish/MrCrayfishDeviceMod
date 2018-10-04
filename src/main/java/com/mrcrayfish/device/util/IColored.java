package com.mrcrayfish.device.util;

import net.minecraft.item.EnumDyeColor;

/**
 * Specifies an object as having the ability to be colored.
 * 
 * <br>
 * </br>
 * 
 * <b>Author: MrCrayfish</b>
 */
public interface IColored
{
	/**
	 * @return The specified {@link EnumDyeColor} of this object
	 */
	EnumDyeColor getColor();

	/**
	 * Sets the color of this object.
	 * 
	 * @param color
	 *            The new color for this object
	 */
	void setColor(EnumDyeColor color);
}
