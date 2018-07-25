package com.mrcrayfish.device.core.client;

import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class ClientNotification implements IToast
{
    private static final ResourceLocation TEXTURE_TOASTS = new ResourceLocation("cdm:textures/gui/toast.png");

    private IIcon icon;
    private String title;
    private String subTitle;

    private ClientNotification() {}

    @Override
    public Visibility draw(GuiToast toastGui, long delta)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        toastGui.drawTexturedModalRect(0, 0, 0, 0, 160, 32);

        if(subTitle == null)
        {
            toastGui.getMinecraft().fontRenderer.drawString(RenderUtil.clipStringToWidth(I18n.format(title), 118), 38, 12, -1, true);
        }
        else
        {
            toastGui.getMinecraft().fontRenderer.drawString(RenderUtil.clipStringToWidth(I18n.format(title), 118), 38, 7, -1, true);
            toastGui.getMinecraft().fontRenderer.drawString(RenderUtil.clipStringToWidth(I18n.format(subTitle), 118), 38, 18, -1);
        }

        toastGui.getMinecraft().getTextureManager().bindTexture(icon.getIconAsset());
        RenderUtil.drawRectWithTexture(6, 6, icon.getU(), icon.getV(), icon.getGridWidth(), icon.getGridHeight(), icon.getIconSize(), icon.getIconSize(), icon.getSourceWidth(), icon.getSourceHeight());

        return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }

    public static ClientNotification loadFromTag(NBTTagCompound tag)
    {
        ClientNotification notification = new ClientNotification();

        int ordinal = tag.getCompoundTag("icon").getInteger("ordinal");
        String className = tag.getCompoundTag("icon").getString("className");

        try
        {
            notification.icon = (IIcon)Class.forName(className).getEnumConstants()[ordinal];
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        notification.title = tag.getString("title");
        if(tag.hasKey("subTitle", Constants.NBT.TAG_STRING))
        {
            notification.subTitle = tag.getString("subTitle");
        }

        return notification;
    }

    public void push()
    {
        Minecraft.getMinecraft().getToastGui().add(this);
    }
}
