package com.mrcrayfish.device.tileentity.render;

import com.mrcrayfish.device.core.network.NetworkDevice;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
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
                final Collection<NetworkDevice> DEVICES = router.getConnectedDevices(Minecraft.getMinecraft().world);


                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                DEVICES.forEach(networkDevice ->
                {
                    BlockPos devicePos = networkDevice.getPos();

                    GL11.glLineWidth(14F);
                    buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    buffer.pos(0.5F, 0.1F, 0.5F).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
                    buffer.pos((devicePos.getX() - routerPos.getX()) + 0.5F, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5F).color(1.0F, 1.0F, 1.0F, 0.35F).endVertex();
                    tessellator.draw();

                    GL11.glLineWidth(4F);
                    buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    buffer.pos(0.5F, 0.1F, 0.5F).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
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
}
