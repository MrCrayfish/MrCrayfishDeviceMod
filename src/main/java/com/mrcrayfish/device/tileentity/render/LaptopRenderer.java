package com.mrcrayfish.device.tileentity.render;

import com.mrcrayfish.device.block.BlockLaptop;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;

public class LaptopRenderer extends TileEntitySpecialRenderer<TileEntityLaptop>
{
	private Minecraft mc = Minecraft.getMinecraft();
	private IBakedModel model = null;
	
	@Override
	public void renderTileEntityAt(TileEntityLaptop te, double x, double y, double z, float partialTicks, int destroyStage) 
	{
		IBlockState state = DeviceBlocks.laptop.getDefaultState().withProperty(BlockLaptop.TYPE, BlockLaptop.Type.SCREEN);
		BlockPos pos = te.getPos();
		
		bindTexture(TextureMap.locationBlocksTexture);
		GlStateManager.pushMatrix();
		{
			GlStateManager.translate(x, y, z);
			GlStateManager.translate(0.5, 0, 0.5);
			GlStateManager.rotate(te.getBlockMetadata() * -90F + 180F, 0, 1, 0);
			GlStateManager.translate(-0.5, 0, -0.5);
			GlStateManager.translate(0, 0.04, 0.23);
			double rotation;
			if(te.rotation > 0 && te.rotation < 112.5) {
				rotation = te.rotation + (te.open ? partialTicks : -partialTicks);
			} else {
				rotation = te.rotation;
			}
			GlStateManager.rotate(te.rotation, 1, 0, 0);
			GlStateManager.translate(0, -0.05, -0.2625);
			
			
			GlStateManager.disableLighting();
			
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer renderer = tessellator.getWorldRenderer();
			renderer.begin(7, DefaultVertexFormats.BLOCK);
			renderer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
			
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			IBakedModel ibakedmodel = mc.getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
			blockrendererdispatcher.getBlockModelRenderer().renderModel(getWorld(), ibakedmodel, state, pos, renderer, false);
			
			renderer.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.draw();
			
			GlStateManager.enableLighting();
		 }
		 GlStateManager.popMatrix();
	}
}
