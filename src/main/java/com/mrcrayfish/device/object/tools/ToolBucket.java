package com.mrcrayfish.device.object.tools;

import com.mrcrayfish.device.object.Canvas;
import com.mrcrayfish.device.object.Tool;

public class ToolBucket extends Tool {

	@Override
	public void handleClick(Canvas canvas, int x, int y) 
	{
		fill(canvas, x, y, canvas.pixels[x][y], canvas.getCurrentColour());
	}

	@Override
	public void handleRelease(Canvas canvas, int x, int y) {}

	@Override
	public void handleDrag(Canvas canvas, int x, int y) {}
	
	public void fill(Canvas canvas, int x, int y, int target, int replacement)
	{
		if(x < 0 || y < 0 || x >= canvas.COLUMNS || y >= canvas.ROWS)
			return;
		
		if(target == replacement)
			return;
		
		if(canvas.pixels[x][y] != target)
			return;
		
		canvas.pixels[x][y] = replacement;
		
		fill(canvas, x + 1, y, target, replacement);
		fill(canvas, x - 1, y, target, replacement);
		fill(canvas, x, y + 1, target, replacement);
		fill(canvas, x, y - 1, target, replacement);
	}

}
