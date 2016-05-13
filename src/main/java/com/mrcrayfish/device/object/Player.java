package com.mrcrayfish.device.object;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.object.tiles.Tile;
import com.mrcrayfish.device.util.GuiHelper;
import com.mrcrayfish.device.util.Vec2d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class Player
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
		
		Tile tile = game.getTile(getPosX(), getPosY());
		if(tile != null && tile.isSlow())
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
		Minecraft.getMinecraft().getTextureManager().bindTexture(Game.ICONS);
		GlStateManager.pushMatrix();
		double px = x + posXPrev + (posX - posXPrev) * partialTicks;
		double py = y + posYPrev + (posY - posYPrev) * partialTicks;
		GlStateManager.translate(px, py, 0);
        GlStateManager.enableBlend();
        float rot = rotationPrev + (rotation - rotationPrev) * partialTicks;
        GlStateManager.rotate(rot, 0, 0, 1);
        GlStateManager.translate(-6.5F, -5, 0);
		GuiHelper.drawModalRectWithUV(0, 0, 0, 0, 13, 10, 16, 13);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
	}
}
