package com.mrcrayfish.device.object.tiles;

import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.Game.Layer;
import com.mrcrayfish.device.util.GuiHelper;

public class TileWheat extends Tile
{
	public TileWheat(int id, int x, int y)
	{
		super(id, x, y);
	}

	@Override
	public void render(Game game, int x, int y)
	public void render(Game game, int x, int y, Layer layer)
	{
		GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.SIZE, game.yPosition + y * Tile.SIZE - 5, this.x * 16, this.y * 16, SIZE, SIZE, 16, 16);
		GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.SIZE, game.yPosition + y * Tile.SIZE - 1, this.x * 16, this.y * 16, SIZE, SIZE, 16, 16);
	}
}
