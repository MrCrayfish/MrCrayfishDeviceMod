package com.mrcrayfish.device.tileentity.render;

import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.BlockPaper;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityPaper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * Author: MrCrayfish
 */
public class PaperRenderer extends TileEntitySpecialRenderer<TileEntityPaper>
{
    @Override
    public void renderTileEntityAt(TileEntityPaper te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0.5, 0.5, 0.5);
            IBlockState state = te.getWorld().getBlockState(te.getPos());
            if(state.getBlock() != DeviceBlocks.PAPER) return;
            GlStateManager.rotate(state.getValue(BlockPaper.FACING).getHorizontalIndex() * -90F + 180F, 0, 1, 0);
            GlStateManager.rotate(0F, 0, 0, 1);
            GlStateManager.translate(-0.5, -0.5, -0.485);
            IPrint print = te.getPrint();
            if(print != null)
            {
                IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                renderer.render(print.toTag());
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityPaper te)
    {
        return super.isGlobalRenderer(te);
    }
}
