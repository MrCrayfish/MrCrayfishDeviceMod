package com.mrcrayfish.device.programs.system.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.component.Slider;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GLHelper;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class Palette extends Component
{
    private ComboBox.Custom<Integer> colourPicker;

    private Color currentColor = Color.RED;

    private Slider colourSlider;

    /**
     * The default constructor for a component.
     * <p>
     * Laying out components is simply relative positioning. So for left (x position),
     * specific how many pixels from the left of the application window you want
     * it to be positioned at. The top is the same, but instead from the top (y position).
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public Palette(int left, int top, ComboBox.Custom<Integer> colourPicker)
    {
        super(left, top);
        this.colourPicker = colourPicker;
    }

    @Override
    protected void init(Layout layout)
    {
        colourSlider = new Slider(5, 58, 52);
        colourSlider.setSlideListener(percentage ->
        {
            if(percentage >= (1.0 / 6.0) * 5.0)
            {
                currentColor = new Color(1.0F, 1.0F - (percentage - (1.0F / 6.0F) * 5.0F) * 6.0F, 0.0F);
            }
            else if(percentage >= (1.0 / 6.0) * 4.0)
            {
                currentColor = new Color((percentage - ((1.0F / 6.0F) * 4.0F)) * 6.0F, 1.0F, 0.0F);
            }
            else if(percentage >= (1.0 / 6.0) * 3.0)
            {
                currentColor = new Color(0.0F, 1.0F, 1.0F - (percentage - ((1.0F / 6.0F) * 3.0F)) * 6.0F);
            }
            else if(percentage >= (1.0 / 6.0) * 2.0)
            {
                currentColor = new Color(0.0F, (percentage - ((1.0F / 6.0F) * 2.0F)) * 6.0F, 1.0F);
            }
            else if(percentage >= (1.0 / 6.0) * 1.0)
            {
                currentColor = new Color(1.0F - (percentage - ((1.0F / 6.0F) * 1.0F)) * 6.0F, 0.0F, 1.0F);
            }
            else if(percentage >= (1.0 / 6.0) * 0.0)
            {
                currentColor = new Color(1.0F, 0.0F, percentage * 6.0F);
            }
        });
        layout.addComponent(colourSlider);
    }

    @Override
    protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        Gui.drawRect(x, y, x + 52, y + 52, Color.DARK_GRAY.getRGB());

        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos((double) x + 1, (double)(y + 1 + 50), 1).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((double)(x + 1 + 50), (double)(y + 1 + 50), 1).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((double)(x + 1 + 50), (double) y + 1, 1).color(currentColor.getRed() / 255F, currentColor.getGreen() / 255F, currentColor.getBlue() / 255F, 1.0F).endVertex();
        buffer.pos((double) x + 1, (double) y + 1, 1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if(mouseButton != 0)
            return;

        if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition + 1, yPosition + 1, xPosition + 51, yPosition + 51))
        {
            colourPicker.setValue(GLHelper.getPixel(mouseX, mouseY).getRGB());
        }
    }
}
