package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public abstract class Module
{
    public abstract String[] getRequiredData();

    public abstract int calculateHeight(Map<String, String> data, int width);

    public abstract void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data);

    //TODO: slideshow module, text area syntax highlighting
}
