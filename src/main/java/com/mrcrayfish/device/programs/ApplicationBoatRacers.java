package com.mrcrayfish.device.programs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Laptop;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ApplicationBoatRacers extends Application 
{
	private Game game;

	public ApplicationBoatRacers() 
	{
		super("boat_racer", "Boat Racers");
		this.setDefaultWidth(320);
		this.setDefaultHeight(160);
	}
	
	@Override
	protected void init(int x, int y) 
	{
		super.init(x, y);
		
		try 
		{
			this.game = new Game(x, y, 0, 0, 320, 160);
			super.addComponent(this.game);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		// TODO Auto-generated method stub

	}

	public static class Game extends Component
	{
		public static final ResourceLocation ICONS = new ResourceLocation("cdm:textures/gui/mine_racer.png");
		
		private int mapWidth, mapHeight;
		private Tile[] map;
		
		private static Tile bedrock = new Tile(3, 0);
		private static Tile grass = new Tile(1, 0);
		private static Tile water = new Tile(2, 0);
		
		static
		{
			grass.setSlow(true);
		}
		
		private Player player = new Player(this);
		
		public Game(int x, int y, int left, int top, int mapWidth, int mapHeight) throws Exception
		{
			super(x, y, left, top);
			
			if(mapWidth % Tile.SIZE != 0 || mapHeight % Tile.SIZE != 0)
				throw new Exception("Width and height need to be a multiple of " + Tile.SIZE);
			
			this.mapWidth = mapWidth / Tile.SIZE;
			this.mapHeight = mapHeight / Tile.SIZE;
			this.map = new Tile[this.mapWidth * this.mapHeight];
			
			loadMap();
		}
		
		public void loadMap()
		{
			try
			{
				InputStream input = getClass().getClassLoader().getResourceAsStream("assets/cdm/map.png");
				BufferedImage image = ImageIO.read(input);
				int width = image.getWidth();
				int height = image.getHeight();
				for(int y = 0; y < height; y++)
				{
					for(int x = 0; x < width; x++)
					{
						int col = image.getRGB(x, y);
						System.out.println(col);
						switch(col)
						{
							case -12566464:
								map[x + y * mapWidth] = bedrock;
								break;
							case -16744690:
								map[x + y * mapWidth] = grass;
								break;
							default:
								map[x + y * mapWidth] = water;
								break;
						}
					}
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
					Tile tile = map[x + y * mapWidth];
					if(tile != null)
					{
						tile.render(xPosition + x * Tile.SIZE , yPosition + y * Tile.SIZE);
					}
				}
			}
			
			player.render(xPosition, yPosition, partialTicks);
		}
		
		public static class Player
		{
			private Game game;
			
			private double posX, posY;
			private double posXPrev, posYPrev;
			private double speed;
			private int rotation, rotationPrev;
			private Vec2d direction;
			private Vec2d velocity;
			
			boolean canMove = false;
			
			public Player(Game game)
			{
				this.game = game;
				direction = new Vec2d(0, 0);
				velocity = new Vec2d(0, 0);
			}
			
			public void tick()
			{
				rotationPrev = rotation;
				posXPrev = posX;
				posYPrev = posY;
				
				if(Keyboard.isKeyDown(Keyboard.KEY_UP))
				{	
					speed += 0.5;
					if(speed >= 3)
					{
						speed = 3;
					}
					if(Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))
					{
						speed += 2;
					}
				}
				else
				{
					speed /= 1.1;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
				{
					rotation -= 8;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
				{
					rotation += 8;
				}
				
				if(game.map[getPosX() + getPosY() * game.mapWidth].isSlow())
				{
					speed *= 0.1;
				}
				
				direction.x = Math.cos(Math.toRadians(rotation));
				direction.y = Math.sin(Math.toRadians(rotation));
				direction.normalise();
				
				velocity.x = direction.x * speed;
				velocity.y = direction.y * speed;
				
				if(canMove = canMove())
				{
					this.posX += velocity.x;
					this.posY += velocity.y;
				}
				else
				{
					speed = 0;
				}
			}
			
			public boolean canMove()
			{
				if(posX + velocity.x <= 0) return false;
				if(posY + velocity.y <= 0) return false;
				if(posX + velocity.x >= game.mapWidth * Tile.SIZE) return false;
				if(posY + velocity.y >= game.mapHeight * Tile.SIZE) return false;
				return true;
			}
			
			public int getPosX()
			{
				return (int) (posX / Tile.SIZE);
			}
			
			public int getPosY()
			{
				return (int) (posY / Tile.SIZE);
			}
			
			public void render(int x, int y, float partialTicks)
			{
				Minecraft.getMinecraft().getTextureManager().bindTexture(ICONS);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x + posXPrev + (posX - posXPrev) * partialTicks, y + posYPrev + (posY - posYPrev) * partialTicks, 0);
		        GlStateManager.enableBlend();
		        float rot = rotationPrev + (rotation - rotationPrev) * partialTicks;
		        GlStateManager.rotate(rot, 0, 0, 1);
		        GlStateManager.translate(-8, -6.5F, 0);
				GuiHelper.drawModalRectWithUV(0, 0, 0, 0, 16, 13, 16, 13);
		        GlStateManager.disableBlend();
		        GlStateManager.popMatrix();
			}
		}
		
		public static class Tile
		{
			public static final int SIZE = 8;
			
			private final int x, y;
			private boolean solid;
			private boolean slow;
			
			public Tile(int x, int y)
			{
				this.x = x;
				this.y = y;
			}
			
			public void render(int x, int y)
			{
				GuiHelper.drawModalRectWithUV(x, y, this.x * 16, this.y * 16, SIZE, SIZE, 16, 16);
			}
			
			public void setSolid(boolean solid)
			{
				this.solid = solid;
			}
			
			public void setSlow(boolean slow)
			{
				this.slow = slow;
			}
			
			public boolean isSolid()
			{
				return solid;
			}
			
			public boolean isSlow()
			{
				return slow;
			}
		}
		
		public static class Vec2d
		{
			public double x, y;
			
			public Vec2d(double x, double y)
			{
				this.x = x;
				this.y = y;
			}
			
			public Vec2d(Vec2d v)
			{
				this.x = v.x;
				this.y = v.y;
			}
			
			public void normalise()
			{
				double length = Math.sqrt((x * x) + (y * y));
				if(length > 1)
				{
					this.x /= length;
					this.y /= length;
				}
			}
			
			@Override
			public boolean equals(Object obj)
			{
				if(obj == null) return false;
				if(obj.getClass() != this.getClass()) return false;
				Vec2d v = (Vec2d) obj;
				return this.x == v.x && this.y == v.y;
			}
		}
	}
}
