package com.mrcrayfish.device.object;

public abstract class Tool 
{
	public abstract void handleClick(Canvas canvas, int x, int y);
	
	public abstract void handleRelease(Canvas canvas, int x, int y);
	
	public abstract void handleDrag(Canvas canvas, int x, int y);
}
