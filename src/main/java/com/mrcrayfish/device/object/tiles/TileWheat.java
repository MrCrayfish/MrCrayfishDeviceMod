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
	public void render(Game game, int x, int y, Layer layer)
	{
		GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 5, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);
		GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 1, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);
	}
}
