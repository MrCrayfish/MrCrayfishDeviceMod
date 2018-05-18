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
    private IIcon icon;
    private String title;
    private String subTitle;

    public Notification(IIcon icon, String title)
    {
        this.icon = icon;
        this.title = title;
    }

    public Notification(IIcon icon, String title, String subTitle)
    {
        this(icon, title);
        this.subTitle = subTitle;
    }

    public NBTTagCompound toTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("title", title);

        if(!StringUtils.isEmpty(subTitle))
        {
            tag.setString("subTitle", subTitle);
        }

        NBTTagCompound tagIcon = new NBTTagCompound();
        tagIcon.setInteger("ordinal", icon.getOrdinal());
        tagIcon.setString("className", icon.getClass().getName());

        tag.setTag("icon", tagIcon);

        return tag;
    }

    public void pushTo(EntityPlayerMP player)
    {
        PacketHandler.INSTANCE.sendTo(new MessageNotification(this), player);
    }
}
