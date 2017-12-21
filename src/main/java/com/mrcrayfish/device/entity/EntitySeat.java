package com.mrcrayfish.device.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class EntitySeat extends Entity
{
    public EntitySeat(World worldIn)
    {
        super(worldIn);
        this.setSize(0.001F, 0.001F);
        this.setInvisible(true);
    }

    public EntitySeat(World worldIn, BlockPos pos, double yOffset)
    {
        this(worldIn);
        this.setPosition(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
    }

    @Override
    protected boolean shouldSetPosAfterLoading()
    {
        return false;
    }

    @Override
    public void onEntityUpdate()
    {
        if(!this.world.isRemote && (!this.isBeingRidden() || this.world.isAirBlock(new BlockPos(this.posX, this.posY, this.posZ))))
        {
            this.setDead();
        }
    }

    @Nullable
    public Entity getControllingPassenger()
    {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}
}
