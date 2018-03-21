package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GLHelper;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class Scrollable extends Component
{
    protected int placeholderColor = new Color(1.0F, 1.0F, 1.0F, 0.35F).getRGB();

    private Layout layout;
    private int width;
    private int height;
    private int scroll;

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
    public Scrollable(int left, int top, Layout layout, int visibleHeight)
    {
        super(left, top);
        this.layout = layout;
        this.width = layout.width;
        this.height = visibleHeight;
    }

    /**
     *
     * @param left
     * @param top
     * @param text
     */
    public Scrollable(int left, int top, Text text, int visibleHeight)
    {
        super(left, top);
        this.setText(text);
        this.width = layout.width;
        this.height = visibleHeight;
    }

    public void setText(Text text)
    {
        text.left = 0;
        text.top = 0;
        this.layout = new Layout(text.width, text.lines.size() * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + (text.lines.size() - 1));
        this.layout.addComponent(text);
        this.width = layout.width;
        this.scroll = 0;
    }

    @Override
    protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        GLHelper.pushScissor(x, y, width, height);
        this.layout.render(laptop, mc, x, y - scroll, mouseX, mouseY, windowActive, partialTicks);
        GLHelper.popScissor();
    }

    @Override
    protected void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive)
    {
        this.layout.renderOverlay(laptop, mc, mouseX, mouseY - scroll, windowActive);
        if(layout.height > height)
        {
            int visibleScrollBarHeight = height;
            int scrollBarHeight = Math.max(20, (int) (height / (float) layout.height * (float) visibleScrollBarHeight));
            float scrollPercentage = MathHelper.clamp(scroll / (float) (layout.height - height), 0.0F, 1.0F);
            int scrollBarY = (int) ((visibleScrollBarHeight - scrollBarHeight) * scrollPercentage);
            int scrollY = yPosition + scrollBarY;
            Gui.drawRect(xPosition + width - 5, scrollY, xPosition + width - 2, scrollY + scrollBarHeight, placeholderColor);
        }
    }

    @Override
    protected void updateComponents(int x, int y)
    {
        super.updateComponents(x, y);
        this.layout.updateComponents(x, y);
    }

    @Override
    protected void handleOnLoad()
    {
        this.layout.handleOnLoad();
    }

    @Override
    protected void handleTick()
    {
        this.layout.handleTick();
    }

    @Override
    protected void handleKeyReleased(char character, int code)
    {
        this.layout.handleKeyReleased(character, code);
    }

    @Override
    protected void handleKeyTyped(char character, int code)
    {
        this.layout.handleKeyTyped(character, code);
    }

    @Override
    protected void handleMouseScroll(int mouseX, int mouseY, boolean direction)
    {
        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, height) && layout.height > height)
        {
            scroll += direction ? -3 : 3;
            if(scroll + height > layout.height)
            {
                scroll = layout.height - height;
            }
            else if(scroll < 0)
            {
                scroll = 0;
            }
        }
    }

    @Override
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, height))
        {
            this.layout.handleMouseClick(mouseX, mouseY - scroll, mouseButton);
        }
    }

    @Override
    protected void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
    {
        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, height))
        {
            this.layout.handleMouseRelease(mouseX, mouseY - scroll, mouseButton);
        }
    }

    @Override
    protected void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
    {
        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, height))
        {
            this.layout.handleMouseDrag(mouseX, mouseY - scroll, mouseButton);
        }
    }
}
