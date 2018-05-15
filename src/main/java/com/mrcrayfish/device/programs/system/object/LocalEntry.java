package com.mrcrayfish.device.programs.system.object;

import com.mrcrayfish.device.object.AppInfo;

/**
 * Author: MrCrayfish
 */
public class LocalEntry implements AppEntry
{
    private AppInfo info;

    public LocalEntry(AppInfo info)
    {
        this.info = info;
    }

    @Override
    public String getId()
    {
        return info.getId().toString();
    }

    @Override
    public String getName()
    {
        return info.getName();
    }

    @Override
    public String getAuthor()
    {
        return info.getAuthor();
    }

    public AppInfo getInfo()
    {
        return info;
    }
}
