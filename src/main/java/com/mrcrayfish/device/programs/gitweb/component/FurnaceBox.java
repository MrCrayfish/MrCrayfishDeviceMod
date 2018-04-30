package com.mrcrayfish.device.programs.gitweb.component;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class FurnaceBox extends Component
{
    private static final ResourceLocation CONTAINER_BOXES_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/container_boxes.png");

    private List<Slot> slots = new ArrayList<>();

    private int progressTimer;
    private int fuelTimer;
    private int fuelTime;

    public FurnaceBox(int left, int top, ItemStack input, ItemStack fuel, ItemStack result)
    {
        super(left, top);
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
        mc.getTextureManager().bindTexture(CONTAINER_BOXES_TEXTURE);
        RenderUtil.drawRectWithTexture(x, y, 0, 68, 130, 68, 130, 68, 256, 256);

        int burnProgress = this.getBurnLeftScaled(13);
        this.drawTexturedModalRect(x + 25, y + 28 + 12 - burnProgress, 130, 81 - burnProgress, 14, burnProgress + 1);

        int cookProgress = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(x + 48, y + 25, 130, 82, cookProgress + 1, 16);

        slots.forEach(slot -> slot.render(x, y));
        slots.forEach(slot -> slot.renderOverlay(laptop, x, y, mouseX, mouseY));
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

    protected class Slot
    {
        private int slotX;
        private int slotY;
        private ItemStack stack;

        public Slot(int slotX, int slotY, ItemStack stack)
        {
            this.slotX = slotX;
            this.slotY = slotY;
            this.stack = stack;
        }

        public void render(int x, int y)
        {
            RenderUtil.renderItem(x + slotX, y + slotY, stack, true);
        }

        public void renderOverlay(Laptop laptop, int x, int y, int mouseX, int mouseY)
        {
            if(GuiHelper.isMouseWithin(mouseX, mouseY, x + slotX, y + slotY, 16, 16))
            {
                if(!stack.isEmpty())
                {
                    net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
                    laptop.drawHoveringText(laptop.getItemToolTip(stack), mouseX, mouseY);
                    net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
                }
            }

            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
        }
    }
}
