package com.mrcrayfish.device.tileentity.render;

import com.mrcrayfish.device.block.BlockOfficeChair;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityOfficeChair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
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
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Author: MrCrayfish
 */
public class OfficeChairRenderer extends TileEntitySpecialRenderer<TileEntityOfficeChair>
{
    private Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void render(TileEntityOfficeChair te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        BlockPos pos = te.getPos();
        IBlockState tempState = te.getWorld().getBlockState(pos);
        if(tempState.getBlock() != DeviceBlocks.OFFICE_CHAIR)
        {
            return;
        }

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);

            GlStateManager.translate(0.5, 0, 0.5);
            GlStateManager.rotate(-te.getRotation(), 0, 1, 0);
            GlStateManager.translate(-0.5, 0, -0.5);

            IBlockState state = tempState.getBlock().getActualState(tempState, te.getWorld(), pos).withProperty(BlockOfficeChair.FACING, EnumFacing.NORTH).withProperty(BlockOfficeChair.TYPE, BlockOfficeChair.Type.SEAT);

            GlStateManager.disableLighting();
            GlStateManager.enableTexture2D();

            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            Tessellator tessellator = Tessellator.getInstance();

            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(7, DefaultVertexFormats.BLOCK);
            buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            IBakedModel ibakedmodel = mc.getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
            blockrendererdispatcher.getBlockModelRenderer().renderModel(getWorld(), ibakedmodel, state, te.getPos(), buffer, false);

            buffer.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();

            GlStateManager.enableLighting();
        }
        GlStateManager.popMatrix();
    }
}
