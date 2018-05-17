package com.mrcrayfish.device.programs.system.object;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RemoteEntry implements AppEntry
{
    public String id;
    public String name;
    public String author;
    public String description;
    public int screenshots;
    public String project_id;

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getAuthor()
    {
        return author;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    @Nullable
    public String getVersion()
    {
        return null;
    }

    @Override
    public String getIcon()
    {
        return null;
    }

    @Override
    public String[] getScreenshots()
    {
        return null;
    }
}
