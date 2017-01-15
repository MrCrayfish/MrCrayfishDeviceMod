package com.mrcrayfish.device.object;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.tiles.Tile;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Game extends Component
{
	public static final ResourceLocation ICONS = new ResourceLocation("cdm:textures/gui/mine_racer.png");

	private static final Map<Integer, Tile> registeredTiles = new HashMap<Integer, Tile>();
	
	public int mapWidth;
	public int mapHeight;
	
	private Tile[][] tiles;
	
	private Player player = new Player(this);
	
	private boolean editorMode = false;
	private Tile currentTile = Tile.grass;
	private Layer currentLayer = Layer.BACKGROUND;
	private boolean renderBackground = true;
	private boolean renderMidgroundLow = true;
	private boolean renderMidgroundHigh = true;
	private boolean renderForeground = true;
	private boolean renderPlayer = true;
	
	public Game(int left, int top, int mapWidth, int mapHeight) throws Exception
	{
		super(left, top);
		
		if(mapWidth % Tile.WIDTH != 0 || mapHeight % Tile.HEIGHT != 0)
			throw new Exception("Width and height need to be a multiple of " + Tile.WIDTH);
		
		this.mapWidth = mapWidth / Tile.WIDTH;
		this.mapHeight = mapHeight / Tile.HEIGHT;
		this.tiles = new Tile[4][this.mapWidth * this.mapHeight];
	}
	
	public void setEditorMode(boolean editorMode)
	{
		this.editorMode = editorMode;
	}
	
	public boolean loadMap(int width, int height, int[][] data)
	{
		if(data.length != 4)
			return false;
			
		if(data[0].length != width * height)
			return false;
		
		this.mapWidth = width;
		this.mapHeight = height;
		this.tiles = new Tile[4][this.mapWidth * this.mapHeight];
		
		for(int layer = 0; layer < data.length; layer++)
		{
			for(int tile = 0; tile < data[0].length; tile++)
			{
				this.tiles[layer][tile] = registeredTiles.get(data[layer][tile]);
			}
		}
		return true;
	}

	@Override
	public void handleTick()
	{
		if(renderPlayer)
		{
			player.tick();
		}
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(editorMode)
		{
			int startX = xPosition;
			int startY = yPosition;
			int endX = startX + mapWidth * Tile.WIDTH;
			int endY = startY + mapHeight * Tile.HEIGHT;
			if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
			{
				int pixelX = (mouseX - startX) / Tile.WIDTH;
				int pixelY = (mouseY - startY) / Tile.HEIGHT;
				if(mouseButton == 0)
					placeTile(pixelX, pixelY, currentTile);
				else if(mouseButton == 1)
					placeTile(pixelX, pixelY, null);
			}
		}
	}
	
	@Override
	public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) 
	{
		if(editorMode)
		{
			int startX = xPosition;
			int startY = yPosition;
			int endX = startX + mapWidth * Tile.WIDTH;
			int endY = startY + mapHeight * Tile.HEIGHT;
			if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
			{
				int pixelX = (mouseX - startX) / Tile.WIDTH;
				int pixelY = (mouseY - startY) / Tile.HEIGHT;
				if(mouseButton == 0)
					placeTile(pixelX, pixelY, currentTile);
				else if(mouseButton == 1)
					placeTile(pixelX, pixelY, null);
			}
		}
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		//long start = System.currentTimeMillis();
		
		if(editorMode)
		{
			drawRect(xPosition - 1, yPosition - 1, xPosition + mapWidth * Tile.WIDTH + 1, yPosition + mapHeight * Tile.HEIGHT + 1, Color.DARK_GRAY.getRGB());
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(ICONS);
		
		if(renderBackground)
		{
			for(int i = 0; i < tiles[0].length; i++)
			{
				Tile tile = tiles[0][i];
				if(tile != null)
				{
					tile.render(this, i % mapWidth, i / mapWidth, Layer.BACKGROUND);
				}
			}
			
			for(int i = 0; i < tiles[0].length; i++)
			{
				Tile tile = tiles[0][i];
				if(tile != null)
				{
					tile.renderForeground(this, i % mapWidth, i / mapWidth, Layer.BACKGROUND);
				}
			}
		}
		
		if(renderMidgroundLow)
		{
			for(int i = 0; i < tiles[1].length; i++)
			{
				Tile tile = tiles[1][i];
				if(tile != null)
				{
					tile.render(this, i % mapWidth, i / mapWidth, Layer.MIDGROUND_LOW);
				}
			}
			
			for(int i = 0; i < tiles[1].length; i++)
			{
				Tile tile = tiles[1][i];
				if(tile != null)
				{
					tile.renderForeground(this, i % mapWidth, i / mapWidth, Layer.MIDGROUND_LOW);
				}
			}
		}
		
		if(renderPlayer)
		{
			player.render(xPosition, yPosition, partialTicks);
		}
		
		mc.getTextureManager().bindTexture(ICONS);
		if(renderMidgroundHigh)
		{
			for(int i = 0; i < tiles[2].length; i++)
			{
				Tile tile = tiles[2][i];
				if(tile != null)
				{
					tile.render(this, i % mapWidth, i / mapWidth, Layer.MIDGROUND_HIGH);
				}
			}
			
			for(int i = 0; i < tiles[2].length; i++)
			{
				Tile tile = tiles[2][i];
				if(tile != null)
				{
					tile.renderForeground(this, i % mapWidth, i / mapWidth, Layer.MIDGROUND_HIGH);
				}
			}
		}
		
		if(renderForeground)
		{
			for(int i = 0; i < tiles[3].length; i++)
			{
				Tile tile = tiles[3][i];
				if(tile != null)
				{
					tile.render(this, i % mapWidth, i / mapWidth, Layer.FOREGROUND);
				}
			}
			
			for(int i = 0; i < tiles[3].length; i++)
			{
				Tile tile = tiles[3][i];
				if(tile != null)
				{
					tile.renderForeground(this, i % mapWidth, i / mapWidth, Layer.FOREGROUND);
				}
			}
		}
		
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
		
		//System.out.println("Rendered game in " + (System.currentTimeMillis() - start));
	}
	
	public static void registerTile(int id, Tile tile)
	{
		registeredTiles.put(id, tile);
	}
	
	public static Map<Integer, Tile> getRegisteredtiles()
	{
		return registeredTiles;
	}
	
	public boolean placeTile(int x, int y, Tile tile)
	{
		int index = x + y * mapWidth;
		if(index >= 0 && index < mapWidth * mapHeight)
		{
			tiles[currentLayer.layer][index] = tile;
			return true;
		}
		return false;
	}
	
	public Tile getTile(Layer layer, int x, int y)
	{
		if(x < 0) return null;
		if(x >= mapWidth) return null;
		
		int index = x + y * mapWidth;
		if(index >= 0 && index < mapWidth * mapHeight)
		{
			return tiles[layer.layer][index];
		}
		return null;
	}
	
	public boolean isFullTile(Layer layer, int x, int y)
	{
		if(x < 0) return true;
		if(x >= mapWidth) return true;
		
		int index = x + y * mapWidth;
		if(index >= 0 && index < mapWidth * mapHeight)
		{
			Tile tile = tiles[layer.layer][index];
			if(tile != null) 
				return tile.isFullTile();
		}
		return true;
	}
	
	public void setCurrentTile(Tile currentTile)
	{
		this.currentTile = currentTile;
	}
	
	public Tile getCurrentTile()
	{
		return currentTile;
	}
	
	public void setCurrentLayer(Layer currentLayer)
	{
		this.currentLayer = currentLayer;
	}
	
	public Layer getCurrentLayer()
	{
		return currentLayer;
	}
	
	public void setRenderBackground(boolean renderBackground)
	{
		this.renderBackground = renderBackground;
	}
	
	public void setRenderForeground(boolean renderForeground)
	{
		this.renderForeground = renderForeground;
	}
	
	public void setRenderPlayer(boolean renderPlayer)
	{
		this.renderPlayer = renderPlayer;
	}
	
	public void nextLayer()
	{
		int next = currentLayer.layer + 1;
		if(next <= Layer.values().length - 1)
		{
			this.currentLayer = Layer.values()[next];
		}
		System.out.println(this.currentLayer.name());
	}
	
	public void prevLayer()
	{
		int prev = currentLayer.layer - 1;
		if(prev >= 0)
		{
			this.currentLayer = Layer.values()[prev];
		}
		System.out.println(this.currentLayer.name());
	}

	public static enum Layer
	{
		BACKGROUND(0, 0), MIDGROUND_LOW(1, 0), MIDGROUND_HIGH(2, 20), FOREGROUND(3, 30);
		
		public int layer;
		public double zLevel;
		
		Layer(int layer, double zLevel)
		{
			this.layer = layer;
			this.zLevel = zLevel;
		}
		
		public Layer up()
		{
			if(layer + 1 <= values().length - 1)
			{
				return values()[layer + 1];
			}
			return this;
		}
		
		public Layer down()
		{
			if(layer - 1 >= 0)
			{
				return values()[layer - 1];
			}
			return this;
		}
	}
	
	// Temp method
	public void fill(Tile tile)
	{
		for(int i = 0; i < tiles[0].length; i++) tiles[0][i] = tile;
	}
}
