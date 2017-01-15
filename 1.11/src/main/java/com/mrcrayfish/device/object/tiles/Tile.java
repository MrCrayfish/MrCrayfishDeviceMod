package com.mrcrayfish.device.object.tiles;

import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.Game.Layer;
import com.mrcrayfish.device.util.GuiHelper;

public class Tile
{
	// Blocks
	public static final Tile water = new TileBlock(0, 2, 0).setCategory(Category.BLOCKS);
	public static final Tile stone = new Tile(1, 2, 1).setCategory(Category.BLOCKS);
	public static final Tile grass = new TileGrass(2, 1, 0).setCategory(Category.BLOCKS);
	public static final Tile dirt = new Tile(3, 6, 1).setCategory(Category.BLOCKS);
	public static final Tile gravel = new TileBlock(4, 7, 0).setCategory(Category.BLOCKS);
	public static final Tile sand = new TileBlock(5, 6, 0).setCategory(Category.BLOCKS);
	public static final Tile log = new TileBlock(6, 3, 0).setCategory(Category.BLOCKS);
	public static final Tile planks_oak = new TileBlock(7, 3, 1).setCategory(Category.BLOCKS);
	public static final Tile leaves_oak = new TileBlock(8, 4, 1).setCategory(Category.BLOCKS);
	public static final Tile brick = new TileBlock(9, 5, 1).setCategory(Category.BLOCKS);
	public static final Tile netherrack = new TileBlock(10, 7, 1).setCategory(Category.BLOCKS);
	public static final Tile soul_sand = new TileBlock(11, 8, 0).setCategory(Category.BLOCKS);
	public static final Tile farm_land = new TileBlock(12, 5, 0).setCategory(Category.BLOCKS);
	
	// Details
	public static final Tile red_flower = new TileFlower(13, 0, 2).setCategory(Category.DECORATION);
	public static final Tile flower_blue_orchid = new TileFlower(14, 0, 3).setCategory(Category.DECORATION);
	public static final Tile flower_oxeye_daisy = new TileFlower(15, 0, 4).setCategory(Category.DECORATION);
	public static final Tile flower_allium = new TileFlower(16, 0, 5).setCategory(Category.DECORATION);
	public static final Tile lily_pad = new Tile(17, 1, 2).setCategory(Category.DECORATION);
	public static final Tile wheat = new TileWheat(18, 2, 2).setCategory(Category.DECORATION);
	public static final Tile cactus = new TileCactus(19).setCategory(Category.DECORATION); //Need tile
	public static final Tile enchantment_table = new TileEnchantmentTable(20, 5, 2).setCategory(Category.DECORATION);
	public static final Tile pumpkin = new TileBlock(21, 7, 2, 8, 2).setCategory(Category.DECORATION);
	public static final Tile wheat_block = new TileBlock(22, 8, 1, 9, 1).setCategory(Category.DECORATION);
	public static final Tile carrot = new TileWheat(23, 2, 3).setCategory(Category.DECORATION);
	public static final Tile netherwart = new TileWheat(24, 3, 3).setCategory(Category.DECORATION);
	

	public static final int WIDTH = 8;
	public static final int HEIGHT = 6;
	
	public final int id;
	public final int x, y;
	
	private boolean hasTop = false;
	public int topX = -1, topY = -1;
	
	private Category category;
	
	public Tile(int id, int x, int y)
	{
		this.id = id;
		this.x = x;
		this.y = y;
		this.topX = x;
		this.topY = y;
		
		Game.registerTile(id, this);
	}
	
	public Tile(int id, int x, int y, int topX, int topY)
	{
		this(id, x, y);
		this.hasTop = true;
		this.topX = topX;
		this.topY = topY;
	}
	
	public Tile setCategory(Category category)
	{
		this.category = category;
		return this;
	}
	
	public void render(Game game, int x, int y, Layer layer)
	{
		RenderUtil.drawRectWithTexture(game.xPosition + x * Tile.WIDTH , game.yPosition + y * Tile.HEIGHT, this.x * 16, this.y * 16, WIDTH, HEIGHT, 16, 16);	
	}
	
	public void renderForeground(Game game, int x, int y, Layer layer) {}
	
	public boolean isFullTile()
	{
		return true;
	}
	
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
