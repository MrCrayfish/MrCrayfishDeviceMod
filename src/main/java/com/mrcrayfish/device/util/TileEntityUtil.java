package com.mrcrayfish.device.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A class that has some methods that help when trying to manage tile entities.
 * 
 * <br>
 * </br>
 * 
 * <b>Author: MrCrayfish</b>
 */
public class TileEntityUtil
{
	/**
	 * Marks the block at the specified position to require an update.
	 * 
	 * @param world
	 *            The world instance
	 * @param pos
	 *            The position of the block
	 */
	public static void markBlockForUpdate(World world, BlockPos pos)
	{
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}
}
