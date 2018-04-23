package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
import net.minecraft.client.gui.Gui;

import java.awt.Color;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class FooterModule extends Module
{
    @Override
    public String[] getRequiredData()
    {
        return new String[] { "title", "sub-title", "home-page"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        return 28;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data)
    {
        Button buttonScroll = new Button(0, 5, Icons.ARROW_UP);
        buttonScroll.left = width - buttonScroll.getWidth() - 5;
        buttonScroll.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                frame.scrollToTop();
            }
        });
        layout.addComponent(buttonScroll);

        Button buttonHome = new Button(0, 5, Icons.HOME);
        buttonHome.left = buttonScroll.left - buttonHome.getWidth() - 3;
        buttonHome.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if(mouseButton == 0)
            {
                frame.loadWebsite(data.get("home-page"));
            }
        });
        layout.addComponent(buttonHome);

        int color = Color.DARK_GRAY.getRGB();
        if(data.containsKey("color"))
        {
            color = Integer.parseInt(data.get("color"));
        }

        String title = data.get("title");
        String subTitle = data.get("sub-title");
        int finalColor = color;
        layout.setBackground((gui, mc, x, y, width1, height, mouseX, mouseY, windowActive) ->
        {
            Gui.drawRect(x, y, x + width1, y + height, finalColor);

            RenderUtil.drawStringClipped(title, x + 5, y + 5, buttonHome.left - 10, -1, true);
            RenderUtil.drawStringClipped(subTitle, x + 5, y + 16, buttonHome.left - 10, Color.LIGHT_GRAY.getRGB(), false);
        });
    }
}
