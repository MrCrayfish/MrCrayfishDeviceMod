package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.core.Settings;

/**
 * Author: MrCrayfish
 */
public interface System
{
    /**
     * Open a context on the screen
     */
    void openContext(Layout layout, int x, int y);

    /**
     * Checks if the system has a context open
     *
     * @return has a context open
     */
    boolean hasContext();

    /**
     * Closes the current context on screen
     */
    void closeContext();

    /**
     * Gets the system settings
     *
     * @return the system settings
     */
    Settings getSettings();
}
