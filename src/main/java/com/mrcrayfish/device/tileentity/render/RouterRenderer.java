package com.mrcrayfish.device.tileentity.render;

import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.block.BlockRouter;
import com.mrcrayfish.device.core.network.NetworkDevice;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import com.mrcrayfish.device.util.CollisionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

/**
 * Author: MrCrayfish
 */
public class RouterRenderer extends TileEntitySpecialRenderer<TileEntityRouter>
{
    @Override
    public void render(TileEntityRouter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if(state.getBlock() != DeviceBlocks.ROUTER)
            return;

        if(te.isDebug())
        {
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(x, y, z);
                Router router = te.getRouter();
                BlockPos routerPos = router.getPos();

                Vec3d linePositions = getLineStartPosition(state);
                final double startLineX = linePositions.x;
                final double startLineY = linePositions.y;
                final double startLineZ = linePositions.z;

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();

                final Collection<NetworkDevice> DEVICES = router.getConnectedDevices(Minecraft.getMinecraft().world);
                DEVICES.forEach(networkDevice ->
                {
                    BlockPos devicePos = networkDevice.getPos();

                    GL11.glLineWidth(14F);
                    buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    buffer.pos(startLineX, startLineY, startLineZ).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    buffer.pos((devicePos.getX() - routerPos.getX()) + 0.5F, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5F).color(1.0F, 1.0F, 1.0F, 0.35F).endVertex();
                    tessellator.draw();

                    GL11.glLineWidth(4F);
                    buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    buffer.pos(startLineX, startLineY, startLineZ).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    buffer.pos((devicePos.getX() - routerPos.getX()) + 0.5F, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5F).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
                    tessellator.draw();
                });
            }
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }
    }

    private Vec3d getLineStartPosition(IBlockState state)
    {
        float lineX = 0.5F;
        float lineY = 0.1F;
        float lineZ = 0.5F;

        if(state.getValue(BlockRouter.VERTICAL))
        {
            double[] fixedPosition = CollisionHelper.fixRotation(state.getValue(BlockPrinter.FACING), 14 * 0.0625, 0.5, 14 * 0.0625, 0.5);
            lineX = (float) fixedPosition[0];
            lineY = 0.35F;
            lineZ = (float) fixedPosition[1];
        }

        return new Vec3d(lineX, lineY, lineZ);
    }
}
