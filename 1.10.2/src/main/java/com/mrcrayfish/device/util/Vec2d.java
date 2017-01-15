package com.mrcrayfish.device.util;

public class Vec2d
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
		if(length > 1)
		{
			this.x /= length;
			this.y /= length;
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		if(obj.getClass() != this.getClass()) return false;
		Vec2d v = (Vec2d) obj;
		return this.x == v.x && this.y == v.y;
	}
}
