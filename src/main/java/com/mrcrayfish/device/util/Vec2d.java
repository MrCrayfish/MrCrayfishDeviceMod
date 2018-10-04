package com.mrcrayfish.device.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A generic Vector that can be used in math.
 * <br>
 * </br>
 * 
 * <b>Author: MrCrayfish</b>
 */
public class Vec2d implements Cloneable, INBTSerializable<NBTTagCompound>
{
	public double x, y;

	public Vec2d(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Vec2d(Vec2d v)
	{
		this.x = v.x;
		this.y = v.y;
	}

	public void normalise()
	{
		double length = Math.sqrt((x * x) + (y * y));
		if (length > 1)
		{
			this.x /= length;
			this.y /= length;
		}
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("x", this.x);
		nbt.setDouble("y", this.y);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey("x", Constants.NBT.TAG_DOUBLE))
		{
			this.x = nbt.getDouble("x");
		}
		
		if (nbt.hasKey("y", Constants.NBT.TAG_DOUBLE))
		{
			this.y = nbt.getDouble("y");
		}
	}

	@Override
	public int hashCode()
	{
        long j = Double.doubleToLongBits(this.x);
        int i = (int)(j ^ j >>> 32);
        j = Double.doubleToLongBits(this.y);
        i = 31 * i + (int)(j ^ j >>> 32);
        return i;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (obj.getClass() != this.getClass())
			return false;
		Vec2d v = (Vec2d) obj;
		return this.x == v.x && this.y == v.y;
	}

	@Override
	public String toString()
	{
		return "(" + this.x + ", " + this.y + ")";
	}

	@Override
	public Vec2d clone() throws CloneNotSupportedException
	{
		return new Vec2d(this);
	}
}
