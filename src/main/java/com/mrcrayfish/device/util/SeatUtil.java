package com.mrcrayfish.device.util;

import java.util.List;

import com.mrcrayfish.device.entity.EntitySeat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Allows the user to sit on certain blocks.
 * 
 * <br>
 * </br>
 * 
 * <b>Author: MrCrayfish</b>
 */
public class SeatUtil
{
	/**
	 * Creates a new seat and puts the player on top.
	 * 
	 * @param world
	 *            The world the block is in
	 * @param pos
	 *            The position to sit at
	 * @param player
	 *            The player to sit down
	 * @param yOffset
	 *            The offset in pixels to go up and down
	 */
	public static void createSeatAndSit(World world, BlockPos pos, EntityPlayer player, double yOffset)
	{
		List<EntitySeat> seats = world.getEntitiesWithinAABB(EntitySeat.class, new AxisAlignedBB(pos));
		if (!seats.isEmpty())
		{
			EntitySeat seat = seats.get(0);
			if (seat.getRidingEntity() == null)
			{
				player.startRiding(seat);
			}
		} else
		{
			EntitySeat seat = new EntitySeat(world, pos, yOffset);
			world.spawnEntity(seat);
			player.startRiding(seat);
		}
	}
}
