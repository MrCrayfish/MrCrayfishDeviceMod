package com.mrcrayfish.device.object;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.object.Game.Layer;
import com.mrcrayfish.device.object.tiles.Tile;
import com.mrcrayfish.device.util.Vec2d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class Player
{
	private static final ResourceLocation boatTextures = new ResourceLocation("textures/entity/boat.png");
	
	private Game game;
	
	private double posX, posY;
	private double posXPrev, posYPrev;
	private double speed;
	private int rotation, rotationPrev;
	private Vec2d direction;
	private Vec2d velocity;
	
	private ModelBoat boatModel;
	private ModelDummyPlayer playerModel;
	
	boolean canMove = false;
	
	public Player(Game game)
	{
		this.game = game;
		this.direction = new Vec2d(0, 0);
		this.velocity = new Vec2d(0, 0);
		this.boatModel = new ModelBoat();
		boolean slim = Minecraft.getMinecraft().player.getSkinType().equals("slim");
		this.playerModel = new ModelDummyPlayer(0F, slim);
		this.playerModel.isRiding = true;
		this.playerModel.isChild = false;
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
		float scale = 0.5F;
		double px = x + posXPrev + (posX - posXPrev) * partialTicks;
		double py = y + posYPrev + (posY - posYPrev) * partialTicks;
        float rot = rotationPrev + (rotation - rotationPrev) * partialTicks;
        
        GlStateManager.pushMatrix();
		GlStateManager.translate((float) px, (float) py, 3.0F);
		GlStateManager.scale((float) (-scale), (float) -scale, (float) -scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); //Flips boat up
		GlStateManager.rotate(90F, 1, 0, 0);
		GlStateManager.translate(0.0F, -3F, 0.0F);
		GlStateManager.rotate(-20F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(boatTextures);
		boatModel.render((Entity) null, 0F, 0F, 0F, 0F, 0F, 1F);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) px, (float) py, 3.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		// //Flips boat up
		GlStateManager.rotate(90F, 1, 0, 0);
		GlStateManager.translate(0.0F, 5.0F, 0.0F);
		GlStateManager.rotate(20F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(rot - 90F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0F, -12F, 5F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().player.getLocationSkin());
		playerModel.render((Entity) null, 0F, 0F, 0F, 0F, 0F, 1F);
		GlStateManager.popMatrix();
	}
	
	public static class ModelDummyPlayer extends ModelBiped
	{
		public ModelRenderer bipedLeftArmwear;
		public ModelRenderer bipedRightArmwear;
		public ModelRenderer bipedLeftLegwear;
		public ModelRenderer bipedRightLegwear;
		public ModelRenderer bipedBodyWear;
		private ModelRenderer bipedCape;
		private ModelRenderer bipedDeadmau5Head;
		private boolean smallArms;

		public ModelDummyPlayer(float scale, boolean slim)
		{
			super(scale, 0.0F, 64, 64);
			this.smallArms = slim;
			this.bipedDeadmau5Head = new ModelRenderer(this, 24, 0);
			this.bipedDeadmau5Head.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, scale);
			this.bipedCape = new ModelRenderer(this, 0, 0);
			this.bipedCape.setTextureSize(64, 32);
			this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, scale);

			if (slim)
			{
				this.bipedLeftArm = new ModelRenderer(this, 32, 48);
				this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale);
				this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.bipedRightArm = new ModelRenderer(this, 40, 16);
				this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
				this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
				this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
				this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
				this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
			}
			else
			{
				this.bipedLeftArm = new ModelRenderer(this, 32, 48);
				this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
				this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
				this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
				this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
				this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
				this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
				this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
			}

			this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
			this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
			this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
			this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
			this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
			this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
			this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
			this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
			this.bipedBodyWear = new ModelRenderer(this, 16, 32);
			this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F);
			this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
		}

		public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
		{
			this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
			GlStateManager.pushMatrix();

			this.bipedHead.render(scale);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            this.bipedHeadwear.render(scale);
			this.bipedLeftLegwear.render(scale);
			this.bipedRightLegwear.render(scale);
			this.bipedLeftArmwear.render(scale);
			this.bipedRightArmwear.render(scale);
			this.bipedBodyWear.render(scale);

			GlStateManager.popMatrix();
		}
		
		public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn)
		{
			super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn);
			copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
			copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
			copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
			copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
			copyModelAngles(this.bipedBody, this.bipedBodyWear);
			this.bipedCape.rotationPointY = 0.0F;
		}
	}
}
