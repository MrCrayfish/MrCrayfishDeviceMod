package com.mrcrayfish.device.programs.system.object;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public interface AppEntry
{
    String getId();
    String getName();
    String getAuthor();
    String getDescription();
    @Nullable String getVersion();
    @Nullable String getIcon();
    @Nullable String[] getScreenshots();
}
