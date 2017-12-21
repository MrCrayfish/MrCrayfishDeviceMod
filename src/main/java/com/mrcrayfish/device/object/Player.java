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

public class Player {
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

	public Player(Game game) {
		this.game = game;
		direction = new Vec2d(0, 0);
		velocity = new Vec2d(0, 0);
		boatModel = new ModelBoat();
		boolean slim = Minecraft.getMinecraft().player.getSkinType().equals("slim");
		playerModel = new ModelDummyPlayer(0F, slim);
		playerModel.isRiding = true;
		playerModel.isChild = false;
	}

	public void tick() {
		rotationPrev = rotation;
		posXPrev = posX;
		posYPrev = posY;

		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			speed += 0.5;
			if (speed >= 3) {
				speed = 3;
			}
			if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
				speed += 2;
			}
		} else {
			speed /= 1.1;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			rotation -= 8;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			rotation += 8;
		}

		Tile tile = game.getTile(Layer.BACKGROUND, getPosX(), getPosY());
		if ((tile != null) && tile.isSlow()) {
			speed *= 0.1;
		}

		direction.x = Math.cos(Math.toRadians(rotation));
		direction.y = Math.sin(Math.toRadians(rotation));
		direction.normalise();

		velocity.x = direction.x * speed;
		velocity.y = direction.y * speed;

		if (canMove = canMove()) {
			posX += velocity.x;
			posY += velocity.y;
		} else {
			speed = 0;
		}
	}

	public boolean canMove() {
		if ((posX + velocity.x) <= 0) {
			return false;
		}
		if ((posY + velocity.y) <= 0) {
			return false;
		}
		if ((posX + velocity.x) >= (game.mapWidth * Tile.WIDTH)) {
			return false;
		}
		if ((posY + velocity.y) >= (game.mapHeight * Tile.HEIGHT)) {
			return false;
		}
		return true;
	}

	public int getPosX() {
		return (int) (posX / Tile.WIDTH);
	}

	public int getPosY() {
		return (int) (posY / Tile.HEIGHT);
	}

	public void render(int x, int y, float partialTicks) {
		float scale = 0.5F;
		double px = x + posXPrev + ((posX - posXPrev) * partialTicks);
		double py = y + posYPrev + ((posY - posYPrev) * partialTicks);
		float rot = rotationPrev + ((rotation - rotationPrev) * partialTicks);

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) px, (float) py, 3.0F);
		GlStateManager.scale((-scale), -scale, -scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); // Flips boat up
		GlStateManager.rotate(90F, 1, 0, 0);
		GlStateManager.translate(0.0F, -3F, 0.0F);
		GlStateManager.rotate(-20F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(boatTextures);
		boatModel.render((Entity) null, 0F, 0F, 0F, 0F, 0F, 1F);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) px, (float) py, 3.0F);
		GlStateManager.scale((-scale), scale, scale);
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

	public static class ModelDummyPlayer extends ModelBiped {
		public ModelRenderer bipedLeftArmwear;
		public ModelRenderer bipedRightArmwear;
		public ModelRenderer bipedLeftLegwear;
		public ModelRenderer bipedRightLegwear;
		public ModelRenderer bipedBodyWear;
		private ModelRenderer bipedCape;
		private ModelRenderer bipedDeadmau5Head;

		public ModelDummyPlayer(float scale, boolean slim) {
			super(scale, 0.0F, 64, 64);
			bipedDeadmau5Head = new ModelRenderer(this, 24, 0);
			bipedDeadmau5Head.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, scale);
			bipedCape = new ModelRenderer(this, 0, 0);
			bipedCape.setTextureSize(64, 32);
			bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, scale);

			if (slim) {
				bipedLeftArm = new ModelRenderer(this, 32, 48);
				bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale);
				bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				bipedRightArm = new ModelRenderer(this, 40, 16);
				bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale);
				bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				bipedLeftArmwear = new ModelRenderer(this, 48, 48);
				bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
				bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
				bipedRightArmwear = new ModelRenderer(this, 40, 32);
				bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
				bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
			} else {
				bipedLeftArm = new ModelRenderer(this, 32, 48);
				bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArmwear = new ModelRenderer(this, 48, 48);
				bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
				bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedRightArmwear = new ModelRenderer(this, 40, 32);
				bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
				bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
			}

			bipedLeftLeg = new ModelRenderer(this, 16, 48);
			bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			bipedLeftLegwear = new ModelRenderer(this, 0, 48);
			bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
			bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
			bipedRightLegwear = new ModelRenderer(this, 0, 32);
			bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
			bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
			bipedBodyWear = new ModelRenderer(this, 16, 32);
			bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F);
			bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
		}

		@Override
		public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
				float p_78088_6_, float scale) {
			setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
			GlStateManager.pushMatrix();

			bipedHead.render(scale);
			bipedBody.render(scale);
			bipedRightArm.render(scale);
			bipedLeftArm.render(scale);
			bipedRightLeg.render(scale);
			bipedLeftLeg.render(scale);
			bipedHeadwear.render(scale);
			bipedLeftLegwear.render(scale);
			bipedRightLegwear.render(scale);
			bipedLeftArmwear.render(scale);
			bipedRightArmwear.render(scale);
			bipedBodyWear.render(scale);

			GlStateManager.popMatrix();
		}

		@Override
		public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_,
				float p_78087_5_, float p_78087_6_, Entity entityIn) {
			super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn);
			copyModelAngles(bipedLeftLeg, bipedLeftLegwear);
			copyModelAngles(bipedRightLeg, bipedRightLegwear);
			copyModelAngles(bipedLeftArm, bipedLeftArmwear);
			copyModelAngles(bipedRightArm, bipedRightArmwear);
			copyModelAngles(bipedBody, bipedBodyWear);
			bipedCape.rotationPointY = 0.0F;
		}
	}
}
