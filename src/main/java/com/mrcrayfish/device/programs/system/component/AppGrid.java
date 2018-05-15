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

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class AppGrid extends Component
{
    private int padding = 5;
    private int horizontalItems;
    private int verticalItems;
    private AppEntry[] apps;
    private ApplicationAppStore store;

    private int itemWidth;
    private int itemHeight;

    private long lastClick = 0;
    private int clickedIndex;

    public AppGrid(int left, int top, int horizontalItems, int verticalItems, AppInfo[] apps, ApplicationAppStore store)
    {
        super(left, top);
        this.horizontalItems = horizontalItems;
        this.verticalItems = verticalItems;
        this.apps = parseLocalEntries(Arrays.asList(apps));
        this.store = store;
        this.itemWidth = (ApplicationAppStore.LAYOUT_WIDTH - padding * 2 - padding * (horizontalItems - 1)) / horizontalItems;
        this.itemHeight = 80;
    }

    public AppGrid(int left, int top, int horizontalItems, int verticalItems, RemoteEntry[] apps, ApplicationAppStore store)
    {
        super(left, top);
        this.horizontalItems = horizontalItems;
        this.verticalItems = verticalItems;
        this.apps = parseRemoteEntries(Arrays.asList(apps));
        this.store = store;
        this.itemWidth = (ApplicationAppStore.LAYOUT_WIDTH - padding * 2 - padding * (horizontalItems - 1)) / horizontalItems;
        this.itemHeight = 80;
    }

    @Override
    protected void init(Layout layout)
    {
        int size = Math.min(apps.length, verticalItems * horizontalItems);
        for(int i = 0; i < size; i++)
        {
            AppEntry entry = apps[i];
            int itemX = left + (i % horizontalItems) * (itemWidth + padding) + padding;
            int itemY = top + (i / horizontalItems) * (itemHeight + padding) + padding;
            layout.addComponent(generateAppTile(entry, itemX, itemY));
        }
    }

    @Override
    protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        int size = Math.min(apps.length, verticalItems * horizontalItems);
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
                    AppEntry entry = apps[i];
                    if(entry instanceof LocalEntry)
                    {
                        store.openApplication(((LocalEntry) entry).getInfo());
                    }
                    //TODO reimplement with new app entry system
                }
                else
                {
                    this.lastClick = System.currentTimeMillis();
                    this.clickedIndex = i;
                }
            }
        }
    }

    private AppEntry[] parseLocalEntries(List<AppInfo> appInfoList)
    {
        List<AppEntry> entries = appInfoList.stream().map(LocalEntry::new).collect(Collectors.toList());
        return entries.toArray(new AppEntry[0]);
    }

    private AppEntry[] parseRemoteEntries(List<RemoteEntry> remoteEntryList)
    {
        final Function<AppEntry, AppEntry> FUNCTION = remoteEntry ->
        {
            AppInfo info = ApplicationManager.getApplication(remoteEntry.getId());
            if(info != null)
            {
                return new LocalEntry(info);
            }
            return remoteEntry;
        };
        List<AppEntry> entries = remoteEntryList.stream().map(FUNCTION).collect(Collectors.toList());
        return entries.toArray(new AppEntry[0]);
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
            Image image = new Image(iconOffset, padding, 14 * 3, 14 * 3, remoteEntry.getImage());
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
}
