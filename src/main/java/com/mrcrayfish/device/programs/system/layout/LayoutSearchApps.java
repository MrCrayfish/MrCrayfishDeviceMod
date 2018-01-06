package com.mrcrayfish.device.programs.system.layout;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.ApplicationAppStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class LayoutSearchApps extends StandardLayout
{
    private static final Color ITEM_BACKGROUND = Color.decode("0x9E9E9E");
    private static final Color ITEM_SELECTED = Color.decode("0x757575");

    private long lastClick = 0;

    public LayoutSearchApps(Application app, Layout previous)
    {
        super("Search", ApplicationAppStore.LAYOUT_WIDTH, ApplicationAppStore.LAYOUT_HEIGHT, app, previous);
    }

    @Override
    public void init()
    {
        super.init();

        ItemList<AppInfo> itemListResults = new ItemList<>(5, 48, ApplicationAppStore.LAYOUT_WIDTH - 10, 5, true);
        itemListResults.setItems(new ArrayList<>(ApplicationManager.getAvailableApps()));
        itemListResults.setListItemRenderer(new ListItemRenderer<AppInfo>(18)
        {
            @Override
            public void render(AppInfo info, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
            {
                Gui.drawRect(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());

                GlStateManager.color(1.0F, 1.0F, 1.0F);
                RenderUtil.drawApplicationIcon(info, x + 2, y + 2);
                RenderUtil.drawStringClipped(info.getName() + TextFormatting.GRAY + " - " + TextFormatting.DARK_GRAY + info.getDescription(), x + 20, y + 5, itemListResults.getWidth() - 22, Color.WHITE.getRGB(), false);
            }
        });
        itemListResults.setItemClickListener((info, index, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                System.out.println(index);
                if(System.currentTimeMillis() - this.lastClick <= 200)
                {
                    openApplication(info);
                }
                else
                {
                    this.lastClick = System.currentTimeMillis();
                }
            }
        });
        this.addComponent(itemListResults);

        TextField textFieldSearch = new TextField(5, 26, ApplicationAppStore.LAYOUT_WIDTH - 10);
        textFieldSearch.setIcon(Icons.SEARCH);
        textFieldSearch.setPlaceholder("...");
        textFieldSearch.setKeyListener(c ->
        {
            Predicate<AppInfo> FILTERED = info -> StringUtils.containsIgnoreCase(info.getName(), textFieldSearch.getText()) || StringUtils.containsIgnoreCase(info.getDescription(), textFieldSearch.getText());
            List<AppInfo> filteredItems = ApplicationManager.getAvailableApps().stream().filter(FILTERED).collect(Collectors.toList());
            itemListResults.setItems(filteredItems);
            return false;
        });
        this.addComponent(textFieldSearch);
    }

    private void openApplication(AppInfo info)
    {
        Layout layout = new LayoutAppPage(null, info);
        app.setCurrentLayout(layout);
        Button btnPrevious = new Button(2, 2, Icons.ARROW_LEFT);
        btnPrevious.setClickListener((mouseX1, mouseY1, mouseButton1) ->
        {
            app.setCurrentLayout(this);
        });
        layout.addComponent(btnPrevious);
    }
}
