package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.task.MessageNotification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringUtils;

/**
 * Author: MrCrayfish
 */
public class Notification
{
    private Icons icon;
    private String title;
    private String subTitle;

    public Notification(Icons icon, String title)
    {
        this.icon = icon;
        this.title = title;
    }

    public Notification(Icons icon, String title, String subTitle)
    {
        this(icon, title);
        this.subTitle = subTitle;
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("icon", icon.ordinal());
        tag.setString("title", title);
        if(!StringUtils.isEmpty(subTitle))
        {
            tag.setString("subTitle", subTitle);
        }
        return tag;
    }

    public void pushTo(EntityPlayerMP player)
    {
        PacketHandler.INSTANCE.sendTo(new MessageNotification(this), player);
    }
}
