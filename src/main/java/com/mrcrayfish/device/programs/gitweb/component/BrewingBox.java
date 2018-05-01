package com.mrcrayfish.device.programs.gitweb.component;

import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class BrewingBox extends ContainerBox
{
    //Copied from GuiBrewingStand. Why do they store the length in an array?
    private static final int[] BUBBLELENGTHS = new int[] {29, 24, 20, 16, 11, 6, 0};

    public static final int HEIGHT = 73;

    private int brewTimer;

    public BrewingBox(ItemStack fuel, ItemStack input, ItemStack[] output)
    {
        super(0, 0, 0, 136, 73, new ItemStack(Items.BREWING_STAND), "Brewing Stand");
        slots.add(new Slot(14, 8, fuel));
        slots.add(new Slot(75, 8, input));
        this.setOutput(output);
    }

    private void setOutput(ItemStack[] output)
    {
        if(output.length > 0)
        {
            slots.add(new Slot(52, 42, output[0]));
        }
        if(output.length > 1)
        {
            slots.add(new Slot(75, 49, output[1]));
        }
        if(output.length > 2)
        {
            slots.add(new Slot(98, 42, output[1]));
        }
    }

    @Override
    protected void handleTick()
    {
        if(--brewTimer < 0)
        {
            brewTimer = 400;
        }
    }

    @Override
    protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);

        mc.getTextureManager().bindTexture(CONTAINER_BOXES_TEXTURE);

        this.drawTexturedModalRect(x + 56, y + 47, 152, 252, 18, 4);

        if (brewTimer > 0)
        {
            int scaledPercent = (int) (28.0F * (1.0F - (float) brewTimer / 400.0F));

            if (scaledPercent > 0)
            {
                this.drawTexturedModalRect(x + 93, y + 19, 152, 223, 9, scaledPercent);
            }

            scaledPercent = BUBBLELENGTHS[brewTimer / 2 % 7];

            if (scaledPercent > 0)
            {
                this.drawTexturedModalRect(x + 59, y + 16 + 29 - scaledPercent, 161, 251 - scaledPercent, 12, scaledPercent);
            }
        }
    }
}
