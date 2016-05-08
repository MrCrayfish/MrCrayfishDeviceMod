package com.mrcrayfish.device.object.tiles;

import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.util.GuiHelper;

public class Tile
{
	public static final Tile grass = new TileGrass(1, 0);
	public static final Tile water = new Tile(2, 0);

	public static final int SIZE = 8;
	
	private final int x, y;
	private boolean solid;
	private boolean slow;
	
	public Tile(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void render(Game game, int x, int y)
	{
		GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.SIZE , game.yPosition + y * Tile.SIZE, this.x * 16, this.y * 16, SIZE, SIZE, 16, 16);
	}
	
	public void setSolid(boolean solid)
	{
		this.solid = solid;
	}
	
	public void setSlow(boolean slow)
	{
		this.slow = slow;
	}
	
	public boolean isSolid()
	{
		return solid;
	}
	
	public boolean isSlow()
	{
		return slow;
	}
}
