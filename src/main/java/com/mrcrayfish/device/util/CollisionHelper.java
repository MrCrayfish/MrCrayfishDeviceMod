package com.mrcrayfish.device.util;

import com.mrcrayfish.device.object.Bounds;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class CollisionHelper
{
	public static AxisAlignedBB getBlockBounds(EnumFacing facing, Bounds bounds)
	{
		double[] fixedBounds = fixRotation(facing, bounds.x1, bounds.z1, bounds.x2, bounds.z2);
		return new AxisAlignedBB(fixedBounds[0], bounds.y1, fixedBounds[1], fixedBounds[2], bounds.y2, fixedBounds[3]);
	}

	public static double[] fixRotation(EnumFacing facing, double var1, double var2, double var3, double var4)
	{
		switch (facing)
		{
		case WEST:
			double var_temp_1 = var1;
			var1 = 1.0F - var3;
			double var_temp_2 = var2;
			var2 = 1.0F - var4;
			var3 = 1.0F - var_temp_1;
			var4 = 1.0F - var_temp_2;
			break;
		case NORTH:
			double var_temp_3 = var1;
			var1 = var2;
			var2 = 1.0F - var3;
			var3 = var4;
			var4 = 1.0F - var_temp_3;
			break;
		case SOUTH:
			double var_temp_4 = var1;
			var1 = 1.0F - var4;
			double var_temp_5 = var2;
			var2 = var_temp_4;
			double var_temp_6 = var3;
			var3 = 1.0F - var_temp_5;
			var4 = var_temp_6;
			break;
		default:
			break;
		}
		return new double[] { var1, var2, var3, var4 };
	}
}
