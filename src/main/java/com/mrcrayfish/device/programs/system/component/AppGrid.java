package com.mrcrayfish.device.programs.system.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.ApplicationAppStore;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.Collection;

/**
 * Author: MrCrayfish
 */
public class AppGrid extends Component
{
    private int horizontalItems;
    private int verticalItems;
    private AppInfo[] apps;
    private ApplicationAppStore store;

    private long lastClick = 0;
    private int clickedIndex;

    public AppGrid(int left, int top, int horizontalItems, int verticalItems, Collection<AppInfo> apps, ApplicationAppStore store)
    {
        super(left, top);
        this.horizontalItems = horizontalItems;
        this.verticalItems = verticalItems;
        this.apps = apps.toArray(new AppInfo[0]);
        this.store = store;
    }

    @Override
    protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        int padding = 5;
        int itemWidth = (ApplicationAppStore.LAYOUT_WIDTH - padding * 2 - padding * (horizontalItems - 1)) / horizontalItems;
        int itemHeight = 80;

        int size = Math.min(apps.length, verticalItems * horizontalItems);
        for(int i = 0; i < size; i++)
        {
            AppInfo info = apps[i];

            int itemX = x + (i % horizontalItems) * (itemWidth + padding) + padding;
            int itemY = y + (i / horizontalItems) * (itemHeight + padding) + padding;
            if(GuiHelper.isMouseWithin(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight))
            {
                Gui.drawRect(itemX, itemY, itemX + itemWidth, itemY + itemHeight, Color.GRAY.getRGB());
                Gui.drawRect(itemX + 1, itemY + 1, itemX + itemWidth - 1, itemY + itemHeight - 1, Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            }

            String clipped = RenderUtil.clipStringToWidth(info.getName(), itemWidth - padding * 2);
            int textOffset = (itemWidth - Laptop.fontRenderer.getStringWidth(clipped)) / 2;
            Laptop.fontRenderer.drawString(clipped, itemX + textOffset, itemY + 50, Color.WHITE.getRGB(), true);

            clipped = RenderUtil.clipStringToWidth(info.getAuthor(), itemWidth - padding * 2);
            textOffset = (itemWidth - Laptop.fontRenderer.getStringWidth(clipped)) / 2;
            Laptop.fontRenderer.drawString(clipped, itemX + textOffset, itemY + 62, Color.WHITE.getRGB(), false);

            int iconOffset = (itemWidth - 14 * 3) / 2;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(Laptop.ICON_TEXTURES);
            RenderUtil.drawRectWithTexture(itemX + iconOffset, itemY + padding, info.getIconU(), info.getIconV(), 14 * 3, 14 * 3, 14, 14, 224, 224);
        }
    }

    @Override
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        int padding = 5;
        int itemWidth = (ApplicationAppStore.LAYOUT_WIDTH - padding * 2 - padding * (horizontalItems - 1)) / horizontalItems;
        int itemHeight = 80;
        int size = Math.min(apps.length, verticalItems * horizontalItems);
        for(int i = 0; i < size; i++)
        {
            int itemX = xPosition + (i % horizontalItems) * (itemWidth + padding) + padding;
            int itemY = yPosition + (i / horizontalItems) * (itemHeight + padding) + padding;
            if(GuiHelper.isMouseWithin(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight))
            {
                if(System.currentTimeMillis() - this.lastClick <= 200 && clickedIndex == i)
                {
                    this.lastClick = 0;
                    store.openApplication(apps[i]);
                }
                else
                {
                    this.lastClick = System.currentTimeMillis();
                    this.clickedIndex = i;
                }
            }
        }
    }
}
