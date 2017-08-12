package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

/**
 * Created by Casey on 06-Aug-17.
 */
public abstract class ComboBox extends Component
{
    private String value = "";
    private boolean opened = false;

    protected boolean hovered;
    protected int width = 80;
    protected int height = 14;

    protected Layout layout;

    public ComboBox(int left, int top)
    {
        super(left, top);
    }

    public ComboBox(int left, int top, int width)
    {
        super(left, top);
        this.width = width;
    }

    @Override
    public void handleTick()
    {
        super.handleTick();
        if(opened && !Laptop.getSystem().hasContext())
        {
            opened = false;
        }
    }

    @Override
    public void init(Layout layout)
    {
        this.layout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> Gui.drawRect(x, y, x + width, y + height, Color.GRAY.getRGB()));
    }

    @Override
    public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(Component.COMPONENTS_GUI);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);

            this.hovered = isInside(mouseX, mouseY) && windowActive;
            int i = this.getHoverState(this.hovered);
            int xOffset = width - height;

            /* Corners */
            RenderUtil.drawRectWithTexture(xPosition + xOffset, yPosition, 96 + i * 5, 12, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(xPosition + height - 2 + xOffset, yPosition, 99 + i * 5, 12, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(xPosition + height - 2 + xOffset, yPosition + height - 2, 99 + i * 5, 15, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(xPosition + xOffset, yPosition + height - 2, 96 + i * 5, 15, 2, 2, 2, 2);

            /* Middles */
            RenderUtil.drawRectWithTexture(xPosition + 2 + xOffset, yPosition, 98 + i * 5, 12, height - 4, 2, 1, 2);
            RenderUtil.drawRectWithTexture(xPosition + height - 2 + xOffset, yPosition + 2, 99 + i * 5, 14, 2, height - 4, 2, 1);
            RenderUtil.drawRectWithTexture(xPosition + 2 + xOffset, yPosition + height - 2, 98 + i * 5, 15, height - 4, 2, 1, 2);
            RenderUtil.drawRectWithTexture(xPosition + xOffset, yPosition + 2, 96 + i * 5, 14, 2, height - 4, 2, 1);

            /* Center */
            RenderUtil.drawRectWithTexture(xPosition + 2 + xOffset, yPosition + 2, 98 + i * 5, 14, height - 4, height - 4, 1, 1);

            /* Icon */
            RenderUtil.drawRectWithTexture(xPosition + xOffset + 3, yPosition + 5, 111, 12, 8, 5, 8, 5);

            /* Box */
            drawHorizontalLine(xPosition, xPosition + xOffset, yPosition, Color.BLACK.getRGB());
            drawHorizontalLine(xPosition, xPosition + xOffset, yPosition + height - 1, Color.BLACK.getRGB());
            drawVerticalLine(xPosition, yPosition, yPosition + height - 1, Color.BLACK.getRGB());
            drawRect(xPosition + 1, yPosition + 1, xPosition + xOffset, yPosition + height - 1, Color.DARK_GRAY.getRGB());

            String text = value;
            int valWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(value);
            if(valWidth > (width - height - 8))
            {
                text = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(text, width - height - 12, false) + "...";
            }
            fontrenderer.drawString(text, xPosition + 3, yPosition + 3, Color.WHITE.getRGB(), true);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if(this.hovered && !this.opened)
        {
            this.opened = true;
            Laptop.getSystem().openContext(this.layout, xPosition, yPosition + 13);
        }
    }

    public abstract String getValue();

    public void updateValue()
    {
        value = getValue();
    }

    protected boolean isInside(int mouseX, int mouseY)
    {
        return mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    protected int getHoverState(boolean mouseOver)
    {
        int i = 1;
        if (!this.enabled)
        {
            i = 0;
        }
        else if (mouseOver)
        {
            i = 2;
        }
        return i;
    }

    public static class List<T> extends ComboBox
    {
        private final ItemList<T> list;
        private T selected;

        public List(int left, int top, T[] items)
        {
            super(left, top);
            this.list = new ItemList<>(0, 0, width, 6);
            this.layout = new Layout(width, getListHeight(list));
            this.setItems(items);
        }

        public List(int left, int top, int width, T[] items)
        {
            super(left, top, width);
            this.list = new ItemList<>(0, 0, width, 6);
            this.layout = new Layout(width, getListHeight(list));
            this.setItems(items);
        }

        @Override
        public void init(Layout layout)
        {
            super.init(layout);
            list.setItemClickListener((t, index, mouseButton) ->
            {
                if(mouseButton == 0)
                {
                    selected = t;
                    updateValue();
                    Laptop.getSystem().closeContext();
                }
            });
            this.layout.addComponent(list);
        }

        @Override
        public String getValue()
        {
            if(this.selected == null)
                return "";
            return this.selected.toString();
        }

        public void setItems(T[] items)
        {
            this.list.removeAll();
            for(T t : items)
            {
                this.list.addItem(t);
            }
            this.selected = this.list.getItem(0);
            this.layout.height = getListHeight(this.list);
            updateValue();
        }

        public T getSelectedItem()
        {
            return selected;
        }

        public void setListItemRenderer(ListItemRenderer<T> renderer)
        {
            this.list.setListItemRenderer(renderer);
            this.layout.width = getListWidth(list);
            this.layout.height = getListHeight(list);
        }

        private static int getListHeight(ItemList list)
        {
            int size = Math.max(2, Math.min(list.visibleItems, list.getItems().size()));
            return (list.renderer != null ? list.renderer.getHeight() : 13) * size + size + 1;
        }

        private static int getListWidth(ItemList list)
        {
            return list.width + 15;
        }
    }

    public static class Custom extends ComboBox
    {
        public <T extends Layout> Custom(int left, int top, T t)
        {
            super(left, top);
            this.layout = t;
        }

        @Override
        public String getValue()
        {
            return "";
        }
    }
}
