package com.mrcrayfish.device.object;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.object.Game.Layer;
import com.mrcrayfish.device.object.tiles.Tile;
import com.mrcrayfish.device.util.GuiHelper;
import com.mrcrayfish.device.util.Vec2d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;

public class Player
{
	private Game game;
	
	private double posX, posY;
	private double posXPrev, posYPrev;
	private double speed;
	private int rotation, rotationPrev;
	private Vec2d direction;
	private Vec2d velocity;
	
	private EntityPlayer player = Minecraft.getMinecraft().thePlayer;
	private Entity boat = new EntityBoat(Minecraft.getMinecraft().theWorld);
	
	boolean canMove = false;
	
	public Player(Game game)
	{
		this.game = game;
		direction = new Vec2d(0, 0);
		velocity = new Vec2d(0, 0);
		boat.riddenByEntity = player;
		player.ridingEntity = boat;
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
		
		Tile tile = game.getTile(Layer.BACKGROUND, getPosX(), getPosY());
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
		if(posX + velocity.x >= game.mapWidth * Tile.WIDTH) return false;
		if(posY + velocity.y >= game.mapHeight * Tile.HEIGHT) return false;
		return true;
	}
	
	public int getPosX()
	{
		return (int) (posX / Tile.WIDTH);
	}
	
	public int getPosY()
	{
		return (int) (posY / Tile.HEIGHT);
	}
	
	public void render(int x, int y, float partialTicks)
	{
		float scale = 8F;
		double px = x + posXPrev + (posX - posXPrev) * partialTicks;
		double py = y + posYPrev + (posY - posYPrev) * partialTicks;
        float rot = rotationPrev + (rotation - rotationPrev) * partialTicks;
        
        GlStateManager.pushMatrix();
		GlStateManager.translate((float) px, (float) py, 3.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); //Flips boat up
		GlStateManager.rotate(90F, 1, 0, 0);
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-35F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-rot, 0.0F, 1.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.renderEntityWithPosYaw(boat, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) px, (float) py, 3.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); //Flips boat up
		GlStateManager.rotate(90F, 1, 0, 0);
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-35F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-rot, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.25, -0.25, 0);
		GlStateManager.rotate(-55F, 0.0F, 1.0F, 0.0F);
		float prevYaw = player.rotationYawHead;
		float prev = player.rotationPitch;
		player.rotationYawHead = -145F;
		player.rotationPitch = 0F;
		rendermanager.renderEntityWithPosYaw(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		player.rotationPitch = prev;
		player.rotationYawHead = prevYaw;
		GlStateManager.popMatrix();
	}
}
