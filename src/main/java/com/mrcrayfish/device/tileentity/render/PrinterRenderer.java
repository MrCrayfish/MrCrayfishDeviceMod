package com.mrcrayfish.device.tileentity.render;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class PrinterRenderer extends TileEntitySpecialRenderer<TileEntityPrinter>
{
    private static final ModelPaper MODEL_PAPER = new ModelPaper();

    @Override
    public void render(TileEntityPrinter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.translate(x, y, z);

            if(te.hasPaper())
            {
                GlStateManager.pushMatrix();
                {
                    GlStateManager.translate(0.5, 0.5, 0.5);
                    IBlockState state = te.getWorld().getBlockState(te.getPos());
                    GlStateManager.rotate(state.getValue(BlockPrinter.FACING).getHorizontalIndex() * -90F, 0, 1, 0);
                    GlStateManager.rotate(22.5F, 1, 0, 0);
                    GlStateManager.translate(0, 0, 0.4);
                    GlStateManager.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    MODEL_PAPER.render(null, 0F, 0F, 0F, 0F, 0F, 0.015625F);
                }
                GlStateManager.popMatrix();
            }

            GlStateManager.pushMatrix();
            {
                if(te.isLoading())
                {
                    GlStateManager.translate(0.5, 0.5, 0.5);
                    IBlockState state1 = te.getWorld().getBlockState(te.getPos());
                    GlStateManager.rotate(state1.getValue(BlockPrinter.FACING).getHorizontalIndex() * -90F, 0, 1, 0);
                    GlStateManager.rotate(22.5F, 1, 0, 0);
                    double progress = Math.max(-0.4, -0.4 + (0.4 * ((double) (te.getRemainingPrintTime() - 10) / 20)));
                    GlStateManager.translate(0, progress, 0.36875);
                    GlStateManager.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    MODEL_PAPER.render(null, 0F, 0F, 0F, 0F, 0F, 0.015625F);
                }
                else if(te.isPrinting())
                {
                    GlStateManager.translate(0.5, 0.078125, 0.5);
                    IBlockState state1 = te.getWorld().getBlockState(te.getPos());
                    GlStateManager.rotate(state1.getValue(BlockPrinter.FACING).getHorizontalIndex() * -90F, 0, 1, 0);
                    GlStateManager.rotate(90F, 1, 0, 0);
                    double progress = -0.35 + (0.50 * ((double) (te.getRemainingPrintTime() - 20) / te.getTotalPrintTime()));
                    GlStateManager.translate(0, progress, 0);
                    GlStateManager.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    MODEL_PAPER.render(null, 0F, 0F, 0F, 0F, 0F, 0.015625F);

                    GlStateManager.translate(0.3225, 0.085, -0.001);
                    GlStateManager.rotate(180F, 0, 1, 0);
                    GlStateManager.scale(0.3, 0.3, 0.3);

                    IPrint print = te.getPrint();
                    IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                    renderer.render(print.toTag());
                }
            }
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            {
                GlStateManager.depthMask(false);
                GlStateManager.translate(0.5, 0.5, 0.5);
                IBlockState state1 = te.getWorld().getBlockState(te.getPos());
                GlStateManager.rotate(state1.getValue(BlockPrinter.FACING).getHorizontalIndex() * -90F, 0, 1, 0);
                GlStateManager.rotate(180F, 0, 1, 0);
                GlStateManager.translate(0.0675, 0.005, -0.032);
                GlStateManager.translate(-6.5 * 0.0625, -3.5 * 0.0625, 3.01 * 0.0625);
                GlStateManager.scale(0.010416667F, -0.010416667F, 0.010416667F);
                GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
                GlStateManager.rotate(22.5F, 1, 0, 0);
                Minecraft.getMinecraft().fontRenderer.drawString(Integer.toString(te.getPaperCount()), 0, 0, Color.WHITE.getRGB());
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.depthMask(true);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, -0.5, 0);
            super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        }
        GlStateManager.popMatrix();
    }

    public static class ModelPaper extends ModelBase
    {
        public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/model/paper.png");

        private ModelRenderer box = new ModelRenderer(this, 0, 0).addBox(0, 0, 0, 22, 30, 1);

        @Override
        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            box.render(scale);
        }
    }
}
