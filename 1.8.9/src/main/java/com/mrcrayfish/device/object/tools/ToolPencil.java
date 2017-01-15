package com.mrcrayfish.device.object.tools;

import com.mrcrayfish.device.object.Canvas;
import com.mrcrayfish.device.object.Tool;

public class ToolPencil extends Tool {

	@Override
	public void handleClick(Canvas canvas, int x, int y) 
	{
		canvas.setPixel(x, y, canvas.getCurrentColour());
	}

	@Override
	public void handleRelease(Canvas canvas, int x, int y) {
		
	}

	@Override
	public void handleDrag(Canvas canvas, int x, int y) 
	{
		canvas.setPixel(x, y, canvas.getCurrentColour());
	}

}
