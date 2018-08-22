package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.core.Settings;
import com.mrcrayfish.device.object.AppInfo;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * An abstract system that can handle apps.
 * 
 * <br>
 * </br>
 * 
 * <b>Author: MrCrayfish</b>
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

    /**
     * Opens the specified application
     *
     * @param info the app info instance of the application to be opened
     */
    void openApplication(AppInfo info);

    /**
     * Opens the specified application with an intent tag
     *
     * @param info the app info instance of the application to be opened
     * @param intentTag the tag to pass data to the initialization of an application
     */
    void openApplication(AppInfo info, @Nullable NBTTagCompound intentTag);

    /**
     * Opens the specified application with a file
     *
     * @param info the app info instance of the application to be opened
     * @param file the file for the application to load
     */
    boolean openApplication(AppInfo info, File file);

    /**
     * Closes the specified application
     *
     * @param info the app info instance of application to close
     */
    void closeApplication(AppInfo info);

    /**
     * Gets all the installed applications
     *
     * @return a collection of installed applications
     */
    Collection<AppInfo> getInstalledApplications();
}
