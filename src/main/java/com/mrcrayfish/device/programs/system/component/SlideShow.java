package com.mrcrayfish.device.programs.system.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.system.object.ImageEntry;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;


/**
 * Author: MrCrayfish
 */
public class SlideShow extends Component
{
    private static final java.awt.Color OVERLAY = new java.awt.Color(0.0F, 0.0F, 0.0F, 0.15F);
    private static final java.awt.Color OVERLAY_HOVER = new java.awt.Color(0.35F, 0.35F, 0.35F, 0.15F);

    private int width;
    private int height;

    private NonNullList<ImageEntry> IMAGES = NonNullList.create();
    private int currentImage = -1;
    private Image image;

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
    public SlideShow(int left, int top, int width, int height)
    {
        super(left, top);
        this.width = width;
        this.height = height;
        this.image = new Image(left, top, width, height);
        this.image.setBorderVisible(true);
    }

    @Override
    protected void init(Layout layout)
    {
        image.setDrawFull(true);
        image.init(layout);
    }

    @Override
    protected void handleOnLoad()
    {
        image.handleOnLoad();
    }

    @Override
    protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        if(!this.visible)
            return;

        image.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);

        if(GuiHelper.isMouseWithin(mouseX, mouseY, x, y, 15, height))
        {
            Gui.drawRect(x, y, x + 15, y + height, OVERLAY_HOVER.getRGB());
        }
        else
        {
            Gui.drawRect(x, y, x + 15, y + height, OVERLAY.getRGB());
        }

        if(GuiHelper.isMouseWithin(mouseX, mouseY, x + width - 15, y, 15, height))
        {
            Gui.drawRect(x + width - 15, y, x + width, y + height, OVERLAY_HOVER.getRGB());
        }
        else
        {
            Gui.drawRect(x + width - 15, y, x + width, y + height, OVERLAY.getRGB());
        }

        Icons.CHEVRON_LEFT.draw(mc, x + 2, y + (height - 10) / 2);
        Icons.CHEVRON_RIGHT.draw(mc, x + 3 + width - 15, y + (height - 10) / 2);
    }

    @Override
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if(!this.visible || !this.enabled || mouseButton != 0)
            return;

        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, 15, height))
        {
            if(currentImage > 0)
            {
                this.setImage(currentImage - 1);
            }
        }

        if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition + width - 15, yPosition, 15, height))
        {
            if(currentImage < IMAGES.size() - 1)
            {
                this.setImage(currentImage + 1);
            }
        }
    }

    public void addImage(ResourceLocation resource)
    {
        IMAGES.add(new ImageEntry(resource));
        if(currentImage == -1)
        {
            this.setImage(0);
        }
    }

    public void addImage(String url)
    {
        IMAGES.add(new ImageEntry(url));
        if(currentImage == -1)
        {
            this.setImage(0);
        }
    }

    public void setImage(int index)
    {
        if(IMAGES.isEmpty() && index < 0 && index >= IMAGES.size())
            return;

        currentImage = index;

        ImageEntry entry = IMAGES.get(index);
        switch(entry.getType())
        {
            case LOCAL:
                image.setImage(entry.getResource());
                break;
            case REMOTE:
                image.setImage(entry.getUrl());
                break;
        }
    }
}
