package com.mrcrayfish.device.api.app.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

/**
 * Author: MrCrayfish
 */
public abstract class ItemRenderer<E>
{
    public abstract void render(E e, Gui gui, Minecraft mc, int x, int y, int width, int height);
}
