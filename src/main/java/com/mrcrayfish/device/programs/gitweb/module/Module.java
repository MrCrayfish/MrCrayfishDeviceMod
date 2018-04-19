package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public abstract class Module
{
    public abstract String[] getRequiredData();

    public abstract int calculateHeight(Map<String, String> data, int width);

    public abstract void generate(Application app, Layout layout, int width, Map<String, String> data);

    //TODO: nav module, footer module, slideshow module, text area syntax highlighting, header (align too), divider
}
