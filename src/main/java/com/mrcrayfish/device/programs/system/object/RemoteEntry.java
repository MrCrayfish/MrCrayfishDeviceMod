package com.mrcrayfish.device.programs.system.object;

/**
 * Author: MrCrayfish
 */
public class RemoteEntry implements AppEntry
{
    private String id;
    private String image;
    private String name;
    private String author;

    public RemoteEntry(String id, String image, String name, String author)
    {
        this.id = id;
        this.image = image;
        this.name = name;
        this.author = author;
    }

    @Override
    public String getId()
    {
        return id;
    }

    public String getImage()
    {
        return image;
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
}
