package com.mrcrayfish.device.core;

import com.mrcrayfish.device.programs.system.object.ColorScheme;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Author: MrCrayfish
 */
public class Settings
{
    private static boolean showAllApps = true;

    private ColorScheme colorScheme = new ColorScheme();

    public static void setShowAllApps(boolean showAllApps)
    {
        Settings.showAllApps = showAllApps;
    }

    public static boolean isShowAllApps()
    {
        return Settings.showAllApps;
    }

    public ColorScheme getColorScheme()
    {
        return colorScheme;
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("showAllApps", showAllApps);
        tag.setTag("colorScheme", colorScheme.toTag());
        return tag;
    }

    public static Settings fromTag(NBTTagCompound tag)
    {
        //showAllApps = tag.getBoolean("showAllApps");

        Settings settings = new Settings();
        settings.colorScheme = ColorScheme.fromTag(tag.getCompoundTag("colorScheme"));
        return settings;
    }
}
