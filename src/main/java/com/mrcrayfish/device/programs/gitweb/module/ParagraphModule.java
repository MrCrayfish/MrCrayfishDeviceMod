package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ParagraphModule extends Module
{
    @Override
    public String[] getRequiredData()
    {
        return new String[] { "text" };
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        return make(data, width).getHeight();
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data)
    {
        layout.addComponent(make(data, width));
    }

    private Text make(Map<String, String> data, int width)
    {
        Text text = new Text(data.get("text"), 0, 0, width);
        int padding = data.containsKey("padding") ? Integer.parseInt(data.get("padding")) : 5;
        text.setPadding(padding);
        return text;
    }
}
