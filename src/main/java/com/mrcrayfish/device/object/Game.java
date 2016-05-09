package com.mrcrayfish.device.object;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Laptop;
import com.mrcrayfish.device.object.tiles.Tile;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Game extends Component
{
	public static final ResourceLocation ICONS = new ResourceLocation("cdm:textures/gui/mine_racer.png");
	
	public int mapWidth;
	public int mapHeight;
	
	private Tile[] backgroundTiles;
	private Tile[] foregroundTiles;
	
	private Player player = new Player(this);
	
	public Game(int x, int y, int left, int top, int mapWidth, int mapHeight) throws Exception
	{
		super(x, y, left, top);
		
		if(mapWidth % Tile.SIZE != 0 || mapHeight % Tile.SIZE != 0)
			throw new Exception("Width and height need to be a multiple of " + Tile.SIZE);
		
		this.mapWidth = mapWidth / Tile.SIZE;
		this.mapHeight = mapHeight / Tile.SIZE;
		this.backgroundTiles = new Tile[this.mapWidth * this.mapHeight];
		this.foregroundTiles = new Tile[this.mapWidth * this.mapHeight];
		
		loadMap();
	}
	
	public void loadMap()
	{
		try
		{
			InputStream input = getClass().getClassLoader().getResourceAsStream("assets/cdm/map.png");
			BufferedImage image = ImageIO.read(input);
			int w = image.getWidth();
			int h = image.getHeight();
			int[] tilesInt = new int[w * h];
			image.getRGB(0, 0, w, h, tilesInt, 0, w);
			
			for(int i = 0; i < tilesInt.length; i++)
			{
				int col = tilesInt[i];
				if(col == 0xFF404040) backgroundTiles[i] = Tile.log;
				if(col == 0xFF007F0E) backgroundTiles[i] = Tile.grass;
				if(col == 0xFF0094FF) backgroundTiles[i] = Tile.water;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void handleTick()
	{
		player.tick();
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		mc.getTextureManager().bindTexture(ICONS);
		for(int y = 0; y < mapHeight; y++)
		{
			for(int x = 0; x < mapWidth; x++)
			{
				Tile tile = backgroundTiles[x + y * mapWidth];
				if(tile != null)
				{
					tile.render(this, x, y);
				}
			}
		}
		
		for(int y = 0; y < mapHeight; y++)
		{
			for(int x = 0; x < mapWidth; x++)
			{
				Tile tile = backgroundTiles[x + y * mapWidth];
				if(tile != null)
				{
					tile.renderForeground(this, x, y);
				}
			}
		}
		
		player.render(xPosition, yPosition, partialTicks);
		
		for(int y = 0; y < mapHeight; y++)
		{
			for(int x = 0; x < mapWidth; x++)
			{
				Tile tile = foregroundTiles[x + y * mapWidth];
				if(tile != null)
				{
					tile.render(this, x, y);
				}
			}
		}
		
		for(int y = 0; y < mapHeight; y++)
		{
			for(int x = 0; x < mapWidth; x++)
			{
				Tile tile = foregroundTiles[x + y * mapWidth];
				if(tile != null)
				{
					tile.renderForeground(this, x, y);
				}
			}
		}
	}
	
	public boolean placeTile(int x, int y, Tile tile)
	{
		int index = x + y * mapWidth;
		if(index >= 0 && index < foregroundTiles.length)
		{
			foregroundTiles[index] = tile;
			return true;
		}
		return false;
	}
	
	public Tile getTile(int x, int y)
	{
		int index = x + y * mapWidth;
		if(index >= 0 && index < backgroundTiles.length)
		{
			return backgroundTiles[index];
		}
		return null;
	}

	
}
