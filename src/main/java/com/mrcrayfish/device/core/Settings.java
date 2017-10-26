package com.mrcrayfish.device.core;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Author: MrCrayfish
 */
public class Settings
{
    private static boolean showAllApps = false;

    public static void setShowAllApps(boolean showAllApps)
    {
        Settings.showAllApps = showAllApps;
    }

    public static boolean isShowAllApps()
    {
        return Settings.showAllApps;
    }

    public NBTTagCompound toTag()
    {
        return null;
    }

    public static Settings fromTag(NBTTagCompound tag)
    {
        return null;
    }
}
