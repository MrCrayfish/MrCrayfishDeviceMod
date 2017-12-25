package com.mrcrayfish.device.tileentity.render;

import com.mrcrayfish.device.block.BlockLaptop;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class LaptopRenderer extends TileEntitySpecialRenderer<TileEntityLaptop>
{
	private Minecraft mc = Minecraft.getMinecraft();

	private EntityItem entityItem = new EntityItem(Minecraft.getMinecraft().world, 0D, 0D, 0D);

	@Override
	public void render(TileEntityLaptop te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		IBlockState state = DeviceBlocks.LAPTOP.getDefaultState().withProperty(BlockLaptop.TYPE, BlockLaptop.Type.SCREEN);
		BlockPos pos = te.getPos();
		
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.pushMatrix();
		{
			GlStateManager.translate(x, y, z);

			if(te.isExternalDriveAttached())
			{
				GlStateManager.pushMatrix();
				{
					GlStateManager.translate(0.5, 0, 0.5);
					GlStateManager.rotate(te.getBlockMetadata() * -90F - 90F, 0, 1, 0);
					GlStateManager.translate(-0.5, 0, -0.5);
					GlStateManager.translate(0.595, -0.2075, -0.005);
					entityItem.hoverStart = 0.0F;
					entityItem.setItem(new ItemStack(DeviceItems.FLASH_DRIVE, 1, te.getExternalDriveColor().getMetadata()));
					Minecraft.getMinecraft().getRenderManager().renderEntity(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
					GlStateManager.translate(0.1, 0, 0);
				}
				GlStateManager.popMatrix();
			}

			GlStateManager.pushMatrix();
			{
				GlStateManager.translate(0.5, 0, 0.5);
				GlStateManager.rotate(te.getBlockMetadata() * -90F + 180F, 0, 1, 0);
				GlStateManager.translate(-0.5, 0, -0.5);
				GlStateManager.translate(0, 0.0625, 0.25);
				GlStateManager.rotate(te.getScreenAngle(partialTicks), 1, 0, 0);

				GlStateManager.disableLighting();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();
				buffer.begin(7, DefaultVertexFormats.BLOCK);
				buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());

				BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
				IBakedModel ibakedmodel = mc.getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
				blockrendererdispatcher.getBlockModelRenderer().renderModel(getWorld(), ibakedmodel, state, pos, buffer, false);

				buffer.setTranslation(0.0D, 0.0D, 0.0D);
				tessellator.draw();
				GlStateManager.enableLighting();
			}
			GlStateManager.popMatrix();
		 }
		 GlStateManager.popMatrix();
	}
}
