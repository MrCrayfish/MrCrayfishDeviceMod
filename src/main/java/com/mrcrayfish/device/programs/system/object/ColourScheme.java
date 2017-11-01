package com.mrcrayfish.device.programs.system.object;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class ColourScheme
{
    public int textColour;
    public int textSecondaryColour;
    public int backgroundColour;
    public int backgroundSecondaryColour;
    public int itemBackgroundColour;
    public int itemHighlightColour;

    public ColourScheme()
    {
        resetDefault();
    }

    public int getTextColour()
    {
        return textColour;
    }

    public void setTextColour(int textColour)
    {
        this.textColour = textColour;
    }

    public int getTextSecondaryColour()
    {
        return textSecondaryColour;
    }

    public void setTextSecondaryColour(int textSecondaryColour)
    {
        this.textSecondaryColour = textSecondaryColour;
    }

    public int getBackgroundColour()
    {
        return backgroundColour;
    }

    public void setBackgroundColour(int backgroundColour)
    {
        this.backgroundColour = backgroundColour;
    }

    public int getBackgroundSecondaryColour()
    {
        return backgroundSecondaryColour;
    }

    public void setBackgroundSecondaryColour(int backgroundSecondaryColour)
    {
        this.backgroundSecondaryColour = backgroundSecondaryColour;
    }

    public int getItemBackgroundColour()
    {
        return itemBackgroundColour;
    }

    public void setItemBackgroundColour(int itemBackgroundColour)
    {
        this.itemBackgroundColour = itemBackgroundColour;
    }

    public int getItemHighlightColour()
    {
        return itemHighlightColour;
    }

    public void setItemHighlightColour(int itemHighlightColour)
    {
        this.itemHighlightColour = itemHighlightColour;
    }

    public void resetDefault()
    {
        textColour = Color.decode("0xFFFFFF").getRGB();
        textSecondaryColour = Color.decode("0x9BEDF2").getRGB();
        backgroundColour = Color.decode("0x535861").getRGB();
        backgroundSecondaryColour = 0;
        itemBackgroundColour = Color.decode("0x9E9E9E").getRGB();
        itemHighlightColour = Color.decode("0x757575").getRGB();
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("textColour", textColour);
        tag.setInteger("textSecondaryColour", textSecondaryColour);
        tag.setInteger("backgroundColour", backgroundColour);
        tag.setInteger("backgroundSecondaryColour", backgroundSecondaryColour);
        tag.setInteger("itemBackgroundColour", itemBackgroundColour);
        tag.setInteger("itemHighlightColour", itemHighlightColour);
        return tag;
    }

    public static ColourScheme fromTag(NBTTagCompound tag)
    {
        ColourScheme scheme = new ColourScheme();
        if(tag.hasKey("textColour", Constants.NBT.TAG_INT))
        {
            scheme.textColour = tag.getInteger("textColour");
        }
        if(tag.hasKey("textSecondaryColour", Constants.NBT.TAG_INT))
        {
            scheme.textSecondaryColour = tag.getInteger("textSecondaryColour");
        }
        if(tag.hasKey("backgroundColour", Constants.NBT.TAG_INT))
        {
            scheme.backgroundColour = tag.getInteger("backgroundColour");
        }
        if(tag.hasKey("backgroundSecondaryColour", Constants.NBT.TAG_INT))
        {
            scheme.backgroundSecondaryColour = tag.getInteger("backgroundSecondaryColour");
        }
        if(tag.hasKey("itemBackgroundColour", Constants.NBT.TAG_INT))
        {
            scheme.itemBackgroundColour = tag.getInteger("itemBackgroundColour");
        }
        if(tag.hasKey("itemHighlightColour", Constants.NBT.TAG_INT))
        {
            scheme.itemHighlightColour = tag.getInteger("itemHighlightColour");
        }
        return scheme;
    }
}
