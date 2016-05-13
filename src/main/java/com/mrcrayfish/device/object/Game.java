package com.mrcrayfish.device.object;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.glu.GLU;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Laptop;
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
	
	private Tile[] backgroundTiles;
	private Tile[] foregroundTiles;
	
	private Player player = new Player(this);
	
	private boolean editorMode = false;
	private Tile currentTile = Tile.grass;
	private Layer currentLayer = Layer.BACKGROUND;
	
	public Game(int x, int y, int left, int top, int mapWidth, int mapHeight) throws Exception
	{
		super(x, y, left, top);
		
		if(mapWidth % Tile.SIZE != 0 || mapHeight % Tile.SIZE != 0)
			throw new Exception("Width and height need to be a multiple of " + Tile.SIZE);
		
		this.mapWidth = mapWidth / Tile.SIZE;
		this.mapHeight = mapHeight / Tile.SIZE;
		this.backgroundTiles = new Tile[this.mapWidth * this.mapHeight];
		this.foregroundTiles = new Tile[this.mapWidth * this.mapHeight];
	}
	
	
	public void setEditorMode(boolean editorMode)
	{
		this.editorMode = editorMode;
	}
	
	public boolean loadMap(int width, int height, int[] data)
	{
		if(width * height != data.length)
			return false;
		
		for(int i = 0; i < data.length; i++)
		{
			int id = data[i];
			if(id == 0xFF404040) backgroundTiles[i] = Tile.log;
			if(id == 0xFF007F0E) backgroundTiles[i] = Tile.grass;
			if(id == 0xFF0094FF) backgroundTiles[i] = Tile.water;
			if(id == 0xFF2D2000) backgroundTiles[i] = Tile.farm_land;
		}
		return true;
	}

	@Override
	public void handleTick()
	{
		if(!editorMode)
		{
			player.tick();
		}
	}
	
	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton)
	{
		if(editorMode)
		{
			int startX = xPosition;
			int startY = yPosition;
			int endX = startX + mapWidth * Tile.SIZE;
			int endY = startY + mapHeight * Tile.SIZE;
			if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
			{
				int pixelX = (mouseX - startX) / Tile.SIZE;
				int pixelY = (mouseY - startY) / Tile.SIZE;
				if(mouseButton == 0)
					placeTile(pixelX, pixelY, currentTile);
				else if(mouseButton == 1)
					placeTile(pixelX, pixelY, null);
			}
		}
	}
	
	@Override
	public void handleDrag(int mouseX, int mouseY, int mouseButton) 
	{
		if(editorMode)
		{
			int startX = xPosition;
			int startY = yPosition;
			int endX = startX + mapWidth * Tile.SIZE;
			int endY = startY + mapHeight * Tile.SIZE;
			if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
			{
				int pixelX = (mouseX - startX) / Tile.SIZE;
				int pixelY = (mouseY - startY) / Tile.SIZE;
				if(mouseButton == 0)
					placeTile(pixelX, pixelY, currentTile);
				else if(mouseButton == 1)
					placeTile(pixelX, pixelY, null);
			}
		}
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		//long start = System.currentTimeMillis();
		
		if(editorMode)
		{
			drawRect(xPosition - 1, yPosition - 1, xPosition + mapWidth * Tile.SIZE + 1, yPosition + mapHeight * Tile.SIZE + 1, Color.DARK_GRAY.getRGB());
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
		
		if(!editorMode)
		{
			player.render(xPosition, yPosition, partialTicks);
		}
		
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
			switch(currentLayer)
			{
			case BACKGROUND:
				backgroundTiles[index] = tile;
				break;
			case FOREGROUND:
				foregroundTiles[index] = tile;
				break;
			}
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

	public static enum Layer
	{
		BACKGROUND, FOREGROUND;
	}
}
