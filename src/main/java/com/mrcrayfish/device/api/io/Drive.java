package com.mrcrayfish.device.api.io;

/**
 * Author: MrCrayfish
 */
public class Drive
{
    private String name;
    private Type type;
    private Folder root;

    private boolean synced = false;

    public Drive(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public Type getType()
    {
        return type;
    }

    public Folder getRoot()
    {
        return root;
    }

    public void syncRoot(Folder root)
    {
        if(!synced)
        {
            this.root = root;
            root.setDrive(this);
            root.validate();
            synced = true;
        }
    }

    public boolean isSynced()
    {
        return synced;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public enum Type
    {
        INTERNAL, EXTERNAL, NETWORK, UNKNOWN;

        public static Type fromString(String type)
        {
            for(Type t : values())
            {
                if(t.toString().equals(type))
                {
                    return t;
                }
            }
            return UNKNOWN;
        }
    }
}
