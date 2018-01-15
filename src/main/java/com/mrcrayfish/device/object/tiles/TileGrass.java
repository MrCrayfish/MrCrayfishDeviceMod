package com.mrcrayfish.device.object.tiles;

import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.Game.Layer;
import org.lwjgl.opengl.GL11;

public class TileGrass extends Tile
{
	public TileGrass(int id, int x, int y)
	{
		super(id, x, y);
	}
	
	@Override
	public void render(Game game, int x, int y, Layer layer)
	{
		super.render(game, x, y, layer);
		if(!game.isFullTile(layer, x, y + 1))
		{
			GL11.glColor4f(0.6F, 0.6F, 0.6F, 1F);
			RenderUtil.drawRectWithTexture(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT + 6, Tile.dirt.x * 16, Tile.dirt.y * 16, Tile.WIDTH, Tile.HEIGHT, 16, 16);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public void renderForeground(Game game, int x, int y, Layer layer)
	{
		super.renderForeground(game, x, y, layer);
		
		if(game.getTile(layer, x, y - 1) == Tile.water)
		{
			RenderUtil.drawRectWithTexture(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 1, 16, 16, 8, 1, 16, 2);
		}
		
		if(game.getTile(layer, x, y + 1) == Tile.water)
		{
			RenderUtil.drawRectWithTexture(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT + 6, 16, 18, 8, 3, 16, 6);
		}
		
		if(game.getTile(layer, x - 1, y) == Tile.water)
		{
			RenderUtil.drawRectWithTexture(game.xPosition + x * Tile.WIDTH - 1, game.yPosition + y * Tile.HEIGHT, 0, 16, 1, 6, 2, 12);
		}
		
		if(game.getTile(layer, x + 1, y) == Tile.water)
		{
			RenderUtil.drawRectWithTexture(game.xPosition + x * Tile.WIDTH + 8, game.yPosition + y * Tile.HEIGHT, 2, 16, 1, 6, 2, 12);
		}
	}
	
	@Override
	public boolean isSlow()
	{
		return true;
	}
	
}
