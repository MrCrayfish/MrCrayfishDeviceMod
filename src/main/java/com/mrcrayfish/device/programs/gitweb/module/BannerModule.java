package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.Label;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BannerModule extends Module
{
    @Override
    public String[] getRequiredData()
    {
        return new String[] { "image" };
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        return 50;
    }

    @Override
    public void generate(Application app, Layout layout, int width, Map<String, String> data)
    {
        int height = calculateHeight(data, width);
        Image image = new Image(0, 0, width, height);
        image.setImage(data.get("image"));
        layout.addComponent(image);

        if(data.containsKey("text"))
        {
            Label label = new Label(data.get("text"), width / 2, (height - 18) / 2);
            label.setAlignment(Component.ALIGN_CENTER);
            label.setScale(2.0F);
            layout.addComponent(label);
        }
    }
}
