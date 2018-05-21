package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.task.MessageNotification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringUtils;

/**
 * The notification class for the notification system.
 *
 * This class is intended to be used only on the server (logical and physical) side only. Typically
 * you'd want to be able to send a notification to anyone on the server. There is two options to
 * perform this, either create a background task on the server (a tick event) or send a
 * {@link com.mrcrayfish.device.api.task.Task} from the client to the server. It is not possible to
 * do this from the client side alone.
 *
 * If a notification is needed to be produced on the client side only, see
 * {@link com.mrcrayfish.device.core.client.ClientNotification}
 */
public class Notification
{
    private IIcon icon;
    private String title;
    private String subTitle;

    /**
     * The default constructor for a notification.
     *
     * @param icon the icon to display
     * @param title the title of the notification
     */
    public Notification(IIcon icon, String title)
    {
        this.icon = icon;
        this.title = title;
    }

    /**
     * The alternate constructor for a notification. This includes a sub title.
     *
     * @param icon the icon to display
     * @param title the title of the notification
     * @param subTitle the sub title of the notification
     */
    public Notification(IIcon icon, String title, String subTitle)
    {
        this(icon, title);
        this.subTitle = subTitle;
    }

    /**
     * Writes the notification to a tag for the client
     *
     * @return the notification tag
     */
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

    /**
     * Sends this notification to the specified player
     *
     * @param player the target player
     */
    public void pushTo(EntityPlayerMP player)
    {
        PacketHandler.INSTANCE.sendTo(new MessageNotification(this), player);
    }
}
