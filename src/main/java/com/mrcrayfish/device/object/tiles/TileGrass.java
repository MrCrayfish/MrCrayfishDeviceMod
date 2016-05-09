package com.mrcrayfish.device.object.tiles;

import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.util.GuiHelper;

public class TileGrass extends Tile
{
	public TileGrass(int x, int y)
	{
		super(x, y);
	}

	@Override
	public void renderForeground(Game game, int x, int y)
	{
		super.renderForeground(game, x, y);
		
		if(game.getTile(x, y - 1) == Tile.water)
		{
			GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.SIZE, game.yPosition + y * Tile.SIZE - 1, 16, 16, 8, 1, 16, 2);
		}
		
		if(game.getTile(x, y + 1) == Tile.water)
		{
			GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.SIZE, game.yPosition + y * Tile.SIZE + 8, 16, 18, 8, 1, 16, 2);
		}
		
		if(game.getTile(x - 1, y) == Tile.water)
		{
			GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.SIZE - 1, game.yPosition + y * Tile.SIZE, 0, 16, 1, 8, 2, 16);
		}
		
		if(game.getTile(x + 1, y) == Tile.water)
		{
			GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.SIZE + 8, game.yPosition + y * Tile.SIZE, 2, 16, 1, 8, 2, 16);
		}
	}
	
	@Override
	public boolean isSlow()
	{
		return true;
	}
	
}
