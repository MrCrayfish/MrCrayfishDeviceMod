package com.mrcrayfish.device.util;

import com.mrcrayfish.device.entity.EntitySeat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class SeatUtil
{
    public static void createSeatAndSit(World worldIn, BlockPos pos, EntityPlayer playerIn, double yOffset)
    {
        if(getNearestSeat(worldIn, pos) == null)
        {
            EntitySeat seat = new EntitySeat(worldIn, pos, yOffset);
            worldIn.spawnEntity(seat);
            playerIn.startRiding(seat);
        }
    }

    public static EntitySeat getNearestSeat(World world, BlockPos pos){
        List<EntitySeat> seats = world.getEntitiesWithinAABB(EntitySeat.class, new AxisAlignedBB(pos));
        if(!seats.isEmpty()){
            return seats.get(0);
        }
        return null;
    }
}
