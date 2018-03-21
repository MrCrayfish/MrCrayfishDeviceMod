package com.mrcrayfish.device.api.app.listener;

import com.mrcrayfish.device.api.app.interfaces.IListener;

/**
 * Author: MrCrayfish
 */
public interface KeyListener extends IListener
{
    boolean onKeyTyped(char c);
}
