package com.mrcrayfish.device.tileentity.render;

import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class PrinterRenderer extends TileEntitySpecialRenderer<TileEntityPrinter>
{
    private static final ItemStack ITEM = new ItemStack(DeviceItems.paper_printed);
    private static final EntityItem ENTITY = new EntityItem(null, 0, 0, 0, ITEM);
    static { ENTITY.hoverStart = 0.0F; }

    @Override
    public void renderTileEntityAt(TileEntityPrinter te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);

            if(te.isPrinting())
            {
                GlStateManager.translate(0.5, -0.415625, 0.5);
                GlStateManager.translate(0, 0.5, 0);
                GlStateManager.rotate(-90F, 1, 0, 0);

                IBlockState state = te.getWorld().getBlockState(te.getPos());
                GlStateManager.rotate(state.getValue(BlockPrinter.FACING).getHorizontalIndex() * 90F, 0, 0, 1);


                GlStateManager.translate(0, -0.5, 0);
                double progress = -0.25 + (0.40 * ((double) te.getRemainingPrintTime() / TileEntityPrinter.PRINT_TIME));
                GlStateManager.translate(0.015625, progress, 0);
                Minecraft.getMinecraft().getRenderManager().doRenderEntity(ENTITY, 0, 0, 0, 0, 0, true);
            }
        }
        GlStateManager.popMatrix();
    }
}
