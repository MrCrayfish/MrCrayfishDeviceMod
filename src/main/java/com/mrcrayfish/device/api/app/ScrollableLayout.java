package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.component.Text;
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
public class ScrollableLayout extends Layout
{
    protected int placeholderColor = new Color(1.0F, 1.0F, 1.0F, 0.35F).getRGB();

    protected int scroll;
    private int visibleHeight;
    private int scrollSpeed = 5;

    public ScrollableLayout(int width, int height, int visibleHeight)
    {
        super(width, height);
        this.visibleHeight = visibleHeight;
    }

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
    public ScrollableLayout(int left, int top, int width, int height, int visibleHeight)
    {
        super(left, top, width, height);
        this.visibleHeight = visibleHeight;
    }

    @Override
    public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        GLHelper.pushScissor(x, y, width, visibleHeight);
        super.render(laptop, mc, x, y - scroll, mouseX, mouseY, windowActive, partialTicks);
        GLHelper.popScissor();
    }

    @Override
    public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive)
    {
        super.renderOverlay(laptop, mc, mouseX, mouseY, windowActive);
        if(this.height > this.visibleHeight)
        {
            int visibleScrollBarHeight = visibleHeight;
            int scrollBarHeight = Math.max(20, (int) (visibleHeight / (float) height * (float) visibleScrollBarHeight));
            float scrollPercentage = MathHelper.clamp(scroll / (float) (height - visibleHeight), 0.0F, 1.0F);
            int scrollBarY = (int) ((visibleScrollBarHeight - scrollBarHeight) * scrollPercentage);
            int scrollY = yPosition + scrollBarY;
            Gui.drawRect(xPosition + width - 5, scrollY, xPosition + width - 2, scrollY + scrollBarHeight, placeholderColor);
        }
    }

    @Override
    public void updateComponents(int x, int y)
    {
        this.xPosition = x + left;
        this.yPosition = y + top;
        for(Component c : components)
        {
            c.updateComponents(x + left, y + top - scroll);
        }
    }

    @Override
    public void handleMouseScroll(int mouseX, int mouseY, boolean direction)
    {
        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, visibleHeight) && height > visibleHeight)
        {
            scroll += direction ? -scrollSpeed : scrollSpeed;
            if(scroll + visibleHeight > height)
            {
                scroll = height - visibleHeight;
            }
            else if(scroll < 0)
            {
                scroll = 0;
            }
            this.updateComponents(xPosition - left, yPosition - top);
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, visibleHeight))
        {
            super.handleMouseClick(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
    {
        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, visibleHeight))
        {
            super.handleMouseRelease(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
    {
        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, visibleHeight))
        {
            super.handleMouseDrag(mouseX, mouseY, mouseButton);
        }
    }

    public static ScrollableLayout create(int left, int top, int width, int visibleHeight, String text)
    {
        Text t = new Text(text, 0, 0, width);
        ScrollableLayout layout = new ScrollableLayout(left, top, t.getWidth(), t.getHeight(), visibleHeight);
        layout.addComponent(t);
        return layout;
    }

    public void setScrollSpeed(int scrollSpeed)
    {
        this.scrollSpeed = scrollSpeed;
    }

    public void resetScroll()
    {
        this.scroll = 0;
        this.updateComponents(xPosition - left, yPosition - top);
    }
}
