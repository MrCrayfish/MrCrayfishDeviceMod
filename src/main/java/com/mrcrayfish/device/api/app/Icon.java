package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.api.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public enum Icon
{
    ARROW_RIGHT,
    ARROW_DOWN,
    ARROW_UP,
    ARROW_LEFT,
    CHECK,
    CROSS,
    PLUS,
    IMPORT,
    EXPORT,
    NEW_FILE,
    NEW_FOLDER,
    LOAD,
    COPY,
    CUT,
    CLIPBOARD,
    SAVE,
    TRASH,
    RENAME,
    EDIT,
    SEARCH,
    RELOAD,
    HOME,
    CHEESE,
    CHEVRON_RIGHT,
    CHEVRON_DOWN,
    CHEVRON_UP,
    CHEVRON_LEFT,
    MAIL,
    BELL,
    LOCK,
    UNLOCK,
    KEY,
    WIFI_HIGH,
    WIFI_MED,
    WIFI_LOW,
    WIFI_NONE,
    PLAY,
    STOP,
    PAUSE,
    PREVIOUS,
    NEXT,
    HELP,
    WARNING,
    ERROR,
    VOLUME_ON,
    VOLUME_OFF,
    STAR_OFF,
    STAR_HALF,
    STAR_ON,
    CHAT,
    EJECT,
    CLOCK;

    public static final ResourceLocation ICON_ASSET = new ResourceLocation("cdm:textures/gui/icons.png");

    public static final int ICON_SIZE = 10;
    public static final int GRID_SIZE = 20;

    public int getU()
    {
        return (ordinal() % GRID_SIZE) * ICON_SIZE;
    }

    public int getV()
    {
        return (ordinal() / GRID_SIZE) * ICON_SIZE;
    }

    public void draw(Minecraft mc, int x, int y)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ICON_ASSET);
        RenderUtil.drawRectWithTexture(x, y, getU(), getV(), ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE, 200, 200);
    }
}
