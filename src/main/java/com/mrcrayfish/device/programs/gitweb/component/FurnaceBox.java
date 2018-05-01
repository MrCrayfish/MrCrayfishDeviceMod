package com.mrcrayfish.device.programs.gitweb.component;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class FurnaceBox extends ContainerBox
{
    public static final int HEIGHT = 68;

    private int progressTimer;
    private int fuelTimer;
    private int fuelTime;

    public FurnaceBox(int left, int top, ItemStack input, ItemStack fuel, ItemStack result)
    {
        super(left, top, 0, 68, HEIGHT, new ItemStack(Blocks.FURNACE), "Furnace");
        slots.add(new Slot(25, 8, input));
        slots.add(new Slot(25, 44, fuel));
        slots.add(new Slot(85, 26, result));
        this.fuelTime = TileEntityFurnace.getItemBurnTime(fuel);
    }

    @Override
    protected void handleTick()
    {
        if(++progressTimer == 200)
        {
            progressTimer = 0;
        }
        if(--fuelTimer <= 0)
        {
            fuelTimer = fuelTime;
        }
    }

    @Override
    protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);

        mc.getTextureManager().bindTexture(CONTAINER_BOXES_TEXTURE);

        int burnProgress = this.getBurnLeftScaled(13);
        this.drawTexturedModalRect(x + 25, y + 28 + 12 - burnProgress, 128, 81 - burnProgress, 14, burnProgress + 1);

        int cookProgress = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(x + 48, y + 25, 128, 82, cookProgress + 1, 16);
    }

    private int getCookProgressScaled(int pixels)
    {
        return this.progressTimer * pixels / 200;
    }

    private int getBurnLeftScaled(int pixels)
    {
        int i = this.fuelTime;
        if(i == 0)
        {
            i = 200;
        }
        return this.fuelTimer * pixels / i + 1;
    }
}
