package com.mrcrayfish.device.programs.system.component;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.ApplicationAppStore;
import com.mrcrayfish.device.programs.system.object.AppEntry;
import com.mrcrayfish.device.programs.system.object.LocalEntry;
import com.mrcrayfish.device.programs.system.object.RemoteEntry;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class AppGrid extends Component
{
    private int padding = 5;
    private int horizontalItems;
    private int verticalItems;
    private List<AppEntry> entries = new ArrayList<>();
    private ApplicationAppStore store;

    private int itemWidth;
    private int itemHeight;

    private long lastClick = 0;
    private int clickedIndex;

    private Layout container;

    public AppGrid(int left, int top, int horizontalItems, int verticalItems, ApplicationAppStore store)
    {
        super(left, top);
        this.horizontalItems = horizontalItems;
        this.verticalItems = verticalItems;
        this.store = store;
        this.itemWidth = (ApplicationAppStore.LAYOUT_WIDTH - padding * 2 - padding * (horizontalItems - 1)) / horizontalItems;
        this.itemHeight = 80;
    }

    @Override
    protected void init(Layout layout)
    {
        container = new Layout(0, 0, ApplicationAppStore.LAYOUT_WIDTH, horizontalItems * itemHeight + (horizontalItems + 1) * padding);
        int size = Math.min(entries.size(), verticalItems * horizontalItems);
        for(int i = 0; i < size; i++)
        {
            AppEntry entry = entries.get(i);
            int itemX = left + (i % horizontalItems) * (itemWidth + padding) + padding;
            int itemY = top + (i / horizontalItems) * (itemHeight + padding) + padding;
            container.addComponent(generateAppTile(entry, itemX, itemY));
        }
        layout.addComponent(container);
    }

    @Override
    protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        int size = Math.min(entries.size(), verticalItems * horizontalItems);
        for(int i = 0; i < size; i++)
        {
            int itemX = x + (i % horizontalItems) * (itemWidth + padding) + padding;
            int itemY = y + (i / horizontalItems) * (itemHeight + padding) + padding;
            if(GuiHelper.isMouseWithin(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight))
            {
                Gui.drawRect(itemX, itemY, itemX + itemWidth, itemY + itemHeight, Color.GRAY.getRGB());
                Gui.drawRect(itemX + 1, itemY + 1, itemX + itemWidth - 1, itemY + itemHeight - 1, Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            }
        }
    }

    @Override
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        int size = Math.min(entries.size(), verticalItems * horizontalItems);
        for(int i = 0; i < size; i++)
        {
            int itemX = xPosition + (i % horizontalItems) * (itemWidth + padding) + padding;
            int itemY = yPosition + (i / horizontalItems) * (itemHeight + padding) + padding;
            if(GuiHelper.isMouseWithin(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight))
            {
                if(System.currentTimeMillis() - this.lastClick <= 200 && clickedIndex == i)
                {
                    this.lastClick = 0;
                    store.openApplication(entries.get(i));
                }
                else
                {
                    this.lastClick = System.currentTimeMillis();
                    this.clickedIndex = i;
                }
            }
        }
    }

    public void addEntry(AppInfo info)
    {
        this.entries.add(new LocalEntry(info));
    }

    public void addEntry(AppEntry entry)
    {
        this.entries.add(adjustEntry(entry));
    }

    private AppEntry adjustEntry(AppEntry entry)
    {
        AppInfo info = ApplicationManager.getApplication(entry.getId());
        if(info != null)
        {
            return new LocalEntry(info);
        }
        return entry;
    }

    private Layout generateAppTile(AppEntry entry, int left, int top)
    {
        Layout layout = new Layout(left, top, itemWidth, itemHeight);

        int iconOffset = (itemWidth - 14 * 3) / 2;
        if(entry instanceof LocalEntry)
        {
            LocalEntry localEntry = (LocalEntry) entry;
            Image image = new Image(iconOffset, padding, 14 * 3, 14 * 3, localEntry.getInfo().getIconU(), localEntry.getInfo().getIconV(), 14, 14, 224, 224, Laptop.ICON_TEXTURES);
            layout.addComponent(image);
        }
        else if(entry instanceof RemoteEntry)
        {
            RemoteEntry remoteEntry = (RemoteEntry) entry;
            ResourceLocation resource = new ResourceLocation(remoteEntry.getId());
            Image image = new Image(iconOffset, padding, 14 * 3, 14 * 3, ApplicationAppStore.CERTIFIED_APPS_URL + "/assets/" + resource.getResourceDomain() + "/" + resource.getResourcePath() + "/icon.png");
            layout.addComponent(image);
        }

        String clippedName = RenderUtil.clipStringToWidth(entry.getName(), itemWidth - padding * 2);
        Label labelName = new Label(clippedName, itemWidth / 2, 50);
        labelName.setAlignment(Component.ALIGN_CENTER);
        layout.addComponent(labelName);

        String clippedAuthor = RenderUtil.clipStringToWidth(entry.getAuthor(), itemWidth - padding * 2);
        Label labelAuthor = new Label(clippedAuthor, itemWidth / 2, 62);
        labelAuthor.setAlignment(Component.ALIGN_CENTER);
        labelAuthor.setShadow(false);
        layout.addComponent(labelAuthor);

        return layout;
    }

    public void reloadIcons()
    {
        if(container != null)
        {
            reloadIcons(container);
        }
    }

    private void reloadIcons(Layout layout)
    {
        layout.components.forEach(component ->
        {
            if(component instanceof Layout)
            {
                reloadIcons((Layout) component);
            }
            else if(component instanceof Image)
            {
                ((Image) component).reload();
            }
        });
    }
}
