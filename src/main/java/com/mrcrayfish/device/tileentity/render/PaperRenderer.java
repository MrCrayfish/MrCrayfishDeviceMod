package com.mrcrayfish.device.tileentity.render;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.BlockPaper;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityPaper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.opengl.GL11;

/**
 * Author: MrCrayfish
 */
public class PaperRenderer extends TileEntitySpecialRenderer<TileEntityPaper>
{
    @Override
    public void render(TileEntityPaper te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0.5, 0.5, 0.5);
            IBlockState state = te.getWorld().getBlockState(te.getPos());
            if(state.getBlock() != DeviceBlocks.PAPER) return;
            GlStateManager.rotate(state.getValue(BlockPaper.FACING).getHorizontalIndex() * -90F + 180F, 0, 1, 0);
            GlStateManager.rotate(-te.getRotation(), 0, 0, 1);
            GlStateManager.translate(-0.5, -0.5, -0.5);

            IPrint print = te.getPrint();
            if(print != null)
            {
                NBTTagCompound data = print.toTag();
                if(data.hasKey("pixels", Constants.NBT.TAG_INT_ARRAY) && data.hasKey("resolution", Constants.NBT.TAG_INT))
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(PrinterRenderer.ModelPaper.TEXTURE);
                    if(DeviceConfig.isRenderPrinted3D() && !data.getBoolean("cut"))
                    {
                        drawCuboid(0, 0, 0, 16, 16, 1);
                    }

                    GlStateManager.translate(0, 0, DeviceConfig.isRenderPrinted3D() ? 0.0625 : 0.005);

                    GlStateManager.pushMatrix();
                    {
                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                        renderer.render(data);
                    }
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    {
                        if(DeviceConfig.isRenderPrinted3D() && data.getBoolean("cut"))
                        {
                            NBTTagCompound tag = print.toTag();
                            drawPixels(tag.getIntArray("pixels"), tag.getInteger("resolution"), tag.getBoolean("cut"));
                        }
                    }
                    GlStateManager.popMatrix();
                }
            }
        }
        GlStateManager.popMatrix();
    }

    private static void drawCuboid(double x, double y, double z, double width, double height, double depth)
    {
        x /= 16;
        y /= 16;
        z /= 16;
        width /= 16;
        height /= 16;
        depth /= 16;
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        drawQuad(x + (1 - width), y, z, x + width + (1 - width), y + height, z, EnumFacing.NORTH);
        drawQuad(x + 1, y, z, x + 1, y + height, z + depth, EnumFacing.EAST);
        drawQuad(x + width + 1 - (width + width), y, z + depth, x + width + 1 - (width + width), y + height, z, EnumFacing.WEST);
        drawQuad(x + (1 - width), y, z + depth, x + width + (1 - width), y, z, EnumFacing.DOWN);
        drawQuad(x + (1 - width), y + height, z, x + width + (1 - width), y, z + depth, EnumFacing.UP);
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
    }

    private static void drawQuad(double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo, EnumFacing facing)
    {
        double textureWidth = Math.abs(xTo - xFrom);
        double textureHeight = Math.abs(yTo - yFrom);
        double textureDepth = Math.abs(zTo - zFrom);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        switch(facing.getAxis())
        {
            case X:
                buffer.pos(xFrom, yFrom, zFrom).tex(1 - xFrom + textureDepth, 1 - yFrom + textureHeight).endVertex();
                buffer.pos(xFrom, yTo, zFrom).tex(1 - xFrom + textureDepth, 1 - yFrom).endVertex();
                buffer.pos(xTo, yTo, zTo).tex(1 - xFrom, 1 - yFrom).endVertex();
                buffer.pos(xTo, yFrom, zTo).tex(1 - xFrom, 1 - yFrom + textureHeight).endVertex();
                break;
            case Y:
                buffer.pos(xFrom, yFrom, zFrom).tex(1 - xFrom + textureWidth, 1 - yFrom + textureDepth).endVertex();
                buffer.pos(xFrom, yFrom, zTo).tex(1 - xFrom + textureWidth, 1 - yFrom).endVertex();
                buffer.pos(xTo, yFrom, zTo).tex(1 - xFrom, 1 - yFrom).endVertex();
                buffer.pos(xTo, yFrom, zFrom).tex(1 - xFrom, 1 - yFrom + textureDepth).endVertex();
                break;
            case Z:
                buffer.pos(xFrom, yFrom, zFrom).tex(1 - xFrom + textureWidth, 1 - yFrom + textureHeight).endVertex();
                buffer.pos(xFrom, yTo, zFrom).tex(1 - xFrom + textureWidth, 1 - yFrom).endVertex();
                buffer.pos(xTo, yTo, zTo).tex(1 - xFrom, 1 - yFrom).endVertex();
                buffer.pos(xTo, yFrom, zTo).tex(1 - xFrom, 1 - yFrom + textureHeight).endVertex();
                break;
        }
        tessellator.draw();
    }

    private static void drawPixels(int[] pixels, int resolution, boolean cut)
    {
        double scale = 16 / (double) resolution;
        for(int i = 0; i < resolution; i++)
        {
            for(int j = 0; j < resolution; j++)
            {
                float a = (float) Math.floor((pixels[j + i * resolution] >> 24 & 255) / 255.0F);
                if(a < 1.0F)
                {
                    if(cut) continue;
                    GlStateManager.color(1.0F, 1.0F, 1.0F);
                }
                else
                {
                    float r = (float) (pixels[j + i * resolution] >> 16 & 255) / 255.0F;
                    float g = (float) (pixels[j + i * resolution] >> 8 & 255) / 255.0F;
                    float b = (float) (pixels[j + i * resolution] & 255) / 255.0F;
                    GlStateManager.color(r, g, b, a);
                }
                drawCuboid(j * scale - (resolution - 1) * scale, -i * scale + (resolution - 1) * scale, -1, scale, scale, 1);
            }
        }
    }
}
