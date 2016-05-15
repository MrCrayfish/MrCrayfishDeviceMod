package com.mrcrayfish.device.object.tiles;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.Game.Layer;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.renderer.GlStateManager;

public class TileBlock extends Tile
{
	public TileBlock(int id, int x, int y)
	{
		super(id, x, y);
	}
	
	@Override
	public void render(Game game, int x, int y, Layer layer)
	{	
		if(game.getTile(layer.up(), x, y - 1) != this || layer == Layer.FOREGROUND)
		{
			GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 6, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);
		}
		
		GL11.glColor4f(0.6F, 0.6F, 0.6F, 1F);
		GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT, this.x * 16, this.y * 16 + 4, WIDTH, 6, 16, 12);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public void renderForeground(Game game, int x, int y, Layer layer)
	{
		/*if(layer.layer > 0)
		{
			GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.SIZE , game.yPosition + y * Tile.SIZE - 2, this.x * 16, this.y * 16, SIZE, 8, 16, 16);
		}*/
	}
}
