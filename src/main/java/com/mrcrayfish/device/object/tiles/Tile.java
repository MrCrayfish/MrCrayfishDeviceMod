package com.mrcrayfish.device.object.tiles;

import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.Game.Layer;
import com.mrcrayfish.device.util.GuiHelper;

public class Tile
{
	// Blocks
	public static final Tile grass = new TileGrass(0, 1, 0).setCategory(Category.BLOCKS);
	public static final Tile water = new TileBlock(1, 2, 0).setCategory(Category.BLOCKS);
	public static final Tile log = new TileBlock(2, 3, 0).setCategory(Category.BLOCKS);
	public static final Tile farm_land = new Tile(3, 5, 0).setCategory(Category.BLOCKS);
	public static final Tile stone = new Tile(4, 2, 1).setCategory(Category.BLOCKS);
	public static final Tile planks_oak = new TileBlock(5, 3, 1).setCategory(Category.BLOCKS);
	public static final Tile leaves_oak = new TileBlock(6, 4, 1).setCategory(Category.BLOCKS);
	public static final Tile brick = new TileBlock(7, 5, 1).setCategory(Category.BLOCKS);
	public static final Tile dirt = new Tile(8, 6, 1).setCategory(Category.BLOCKS);
	public static final Tile sand = new TileBlock(9, 6, 0).setCategory(Category.BLOCKS);
	public static final Tile gravel = new TileBlock(10, 7, 0).setCategory(Category.BLOCKS);
	public static final Tile netherrak = new TileBlock(11, 7, 1).setCategory(Category.BLOCKS);
	public static final Tile soul_sand = new TileBlock(12, 8, 0).setCategory(Category.BLOCKS);
	
	// Details
	public static final Tile red_flower = new Tile(13, 0, 2).setCategory(Category.DECORATION);
	public static final Tile flower_blue_orchid = new Tile(14, 0, 3).setCategory(Category.DECORATION);
	public static final Tile flower_oxeye_daisy = new Tile(15, 0, 4).setCategory(Category.DECORATION);
	public static final Tile flower_allium = new Tile(16, 0, 5).setCategory(Category.DECORATION);
	public static final Tile lily_pad = new Tile(17, 1, 2).setCategory(Category.DECORATION);
	public static final Tile wheat = new TileWheat(18, 2, 2).setCategory(Category.DECORATION);
	public static final Tile cactus = new Tile(19, 3, 2).setCategory(Category.DECORATION); //Need tile
	public static final Tile enchantment_table = new Tile(20, 5, 2).setCategory(Category.DECORATION); //Need tile
	public static final Tile pumpkin = new Tile(21, 7, 2, 8, 2).setCategory(Category.DECORATION);
	public static final Tile wheat_block = new Tile(22, 8, 1, 9, 1).setCategory(Category.DECORATION);
	public static final Tile carrot = new TileWheat(23, 2, 3).setCategory(Category.DECORATION);
	public static final Tile netherwart = new TileWheat(24, 3, 3).setCategory(Category.DECORATION);
	

	public static final int WIDTH = 8;
	public static final int HEIGHT = 6;
	
	public final int id;
	public final int x, y;
	
	private Category category;
	
	public Tile(int id, int x, int y)
	{
		this.id = id;
		this.x = x;
		this.y = y;
		
		Game.registerTile(id, this);
	}
	
	public Tile setCategory(Category category)
	{
		this.category = category;
		return this;
	}
	
	public void render(Game game, int x, int y, Layer layer)
	{
		GuiHelper.drawModalRectWithUV(game.xPosition + x * Tile.WIDTH , game.yPosition + y * Tile.HEIGHT, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);	
	}
	
	public void renderForeground(Game game, int x, int y, Layer layer) {}
	
	public boolean isSolid()
	{
		return false;
	}
	
	public boolean isSlow()
	{
		return false;
	}

	public Category getCategory()
	{
		return category;
	}
	
	public static enum Category 
	{
		BLOCKS("Blocks"), DECORATION("Decorations"), GAME("Game");
		
		public String name;
		
		private Category(String name)
		{
			this.name = name;
		}
	}
}
