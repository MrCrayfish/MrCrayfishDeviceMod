package com.mrcrayfish.device.object.tiles;

import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.util.GuiHelper;

public class Tile
{
	// Blocks
	public static final Tile grass = new TileGrass(1, 0);
	public static final Tile water = new Tile(2, 0);
	public static final Tile log = new Tile(3, 0);
	public static final Tile farm_land = new Tile(5, 0);
	
	// Details
	public static final Tile red_flower = new Tile(0, 2);
	public static final Tile lily_pad = new Tile(1, 2);
	public static final Tile wheat = new TileWheat(2, 2);

	public static final int SIZE = 8;
	
	protected final int x;
	protected final int y;
	
	public Tile(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void render(Game game, int x, int y)
	{
		GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.SIZE , game.yPosition + y * Tile.SIZE, this.x * 16, this.y * 16, SIZE, SIZE, 16, 16);
	}
	
	public void renderForeground(Game game, int x, int y) {}
	
	public boolean isSolid()
	{
		return false;
	}
	
	public boolean isSlow()
	{
		return false;
	}
}
