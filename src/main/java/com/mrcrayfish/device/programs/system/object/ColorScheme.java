package com.mrcrayfish.device.programs.system.object;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class ColorScheme
{
    public int textColor;
    public int textSecondaryColor;
    public int headerColor;
    public int backgroundColor;
    public int backgroundSecondaryColor;
    public int itemBackgroundColor;
    public int itemHighlightColor;

    public ColorScheme()
    {
        resetDefault();
    }

    public int getTextColor()
    {
        return textColor;
    }

    public void setTextColor(int textColor)
    {
        this.textColor = textColor;
    }

    public int getTextSecondaryColor()
    {
        return textSecondaryColor;
    }

    public void setTextSecondaryColor(int textSecondaryColor)
    {
        this.textSecondaryColor = textSecondaryColor;
    }

    public int getHeaderColor()
    {
        return headerColor;
    }

    public void setHeaderColor(int headerColor)
    {
        this.headerColor = headerColor;
    }

    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundSecondaryColor()
    {
        return backgroundSecondaryColor;
    }

    public void setBackgroundSecondaryColor(int backgroundSecondaryColor)
    {
        this.backgroundSecondaryColor = backgroundSecondaryColor;
    }

    public int getItemBackgroundColor()
    {
        return itemBackgroundColor;
    }

    public void setItemBackgroundColor(int itemBackgroundColor)
    {
        this.itemBackgroundColor = itemBackgroundColor;
    }

    public int getItemHighlightColor()
    {
        return itemHighlightColor;
    }

    public void setItemHighlightColor(int itemHighlightColor)
    {
        this.itemHighlightColor = itemHighlightColor;
    }

    public void resetDefault()
    {
        textColor = Color.decode("0xFFFFFF").getRGB();
        textSecondaryColor = Color.decode("0x9BEDF2").getRGB();
        headerColor = Color.decode("0x959fa6").getRGB();
        backgroundColor = Color.decode("0x535861").getRGB();
        backgroundSecondaryColor = 0;
        itemBackgroundColor = Color.decode("0x9E9E9E").getRGB();
        itemHighlightColor = Color.decode("0x757575").getRGB();
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("textColor", textColor);
        tag.setInteger("textSecondaryColor", textSecondaryColor);
        tag.setInteger("headerColor", headerColor);
        tag.setInteger("backgroundColor", backgroundColor);
        tag.setInteger("backgroundSecondaryColor", backgroundSecondaryColor);
        tag.setInteger("itemBackgroundColor", itemBackgroundColor);
        tag.setInteger("itemHighlightColor", itemHighlightColor);
        return tag;
    }

    public static ColorScheme fromTag(NBTTagCompound tag)
    {
        ColorScheme scheme = new ColorScheme();
        if(tag.hasKey("textColor", Constants.NBT.TAG_INT))
        {
            scheme.textColor = tag.getInteger("textColor");
        }
        if(tag.hasKey("textSecondaryColor", Constants.NBT.TAG_INT))
        {
            scheme.textSecondaryColor = tag.getInteger("textSecondaryColor");
        }
        if(tag.hasKey("headerColor", Constants.NBT.TAG_INT))
        {
            scheme.headerColor = tag.getInteger("headerColor");
        }
        if(tag.hasKey("backgroundColor", Constants.NBT.TAG_INT))
        {
            scheme.backgroundColor = tag.getInteger("backgroundColor");
        }
        if(tag.hasKey("backgroundSecondaryColor", Constants.NBT.TAG_INT))
        {
            scheme.backgroundSecondaryColor = tag.getInteger("backgroundSecondaryColor");
        }
        if(tag.hasKey("itemBackgroundColor", Constants.NBT.TAG_INT))
        {
            scheme.itemBackgroundColor = tag.getInteger("itemBackgroundColor");
        }
        if(tag.hasKey("itemHighlightColor", Constants.NBT.TAG_INT))
        {
            scheme.itemHighlightColor = tag.getInteger("itemHighlightColor");
        }
        return scheme;
    }
}
