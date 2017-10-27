package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.api.app.IIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public enum Icon extends IIcon
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
    CLOCK,
    SHOPPING_CART,
    SHOPPING_CART_ADD,
    SHOPPING_CART_REMOVE,
    USER,
    USER_ADD,
    USER_REMOVE,
    COMMUNITY,
    SHARE,
    CONTACTS,
    COMPUTER,
    PRINTER,
    GAME_CONTROLLER,
    CAMERA,
    HEADPHONES,
    TELEVISION,
    SMART_PHONE,
    USB,
    INTERNAL_DRIVE,
    EXTERNAL_DRIVE,
    NETWORK_DRIVE,
    DATABASE,
    CD,
    BATTERY_FULL,
    BATTERY_HALF,
    BATTERY_LOW,
    BATTERY_EMPTY,
    POWER_ON,
    POWER_OFF,
    EARTH,
    PICTURE,
    SHOP,
    HOME,
    THUMBS_UP_OFF,
    THUMBS_UP_ON,
    THUMBS_DOWN_OFF,
    THUMBS_DOWN_ON,
    BOOKMARK_OFF,
    BOOKMARK_ON,
    UNDO,
    REDO,
    WRENCH,
    FORBIDDEN,
    MUSIC,
    EYE_DROPPER,
    DOTS_VERTICAL,
    DOTS_HORIZONTAL,
    EXPAND,
    SHRINK,
    SORT,
    FONT;

    public static final ResourceLocation ICON_ASSET = new ResourceLocation("cdm:textures/gui/icons.png");

    public static final int ICON_SIZE = 10;
    public static final int GRID_SIZE = 20;

	@Override
	public ResourceLocation getIconAsset() {
		return ICON_ASSET;
	}

	@Override
	public int getIconSize() {
		return ICON_SIZE;
	}

	@Override
	public int getGridSize() {
		return GRID_SIZE;
	}

    @Override
    public int getU()
    {
        return (ordinal() % GRID_SIZE) * ICON_SIZE;
    }

    @Override
    public int getV()
    {
        return (ordinal() / GRID_SIZE) * ICON_SIZE;
    }

    @Override
    public void draw(Minecraft mc, int x, int y)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ICON_ASSET);
        RenderUtil.drawRectWithTexture(x, y, getU(), getV(), ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE, 200, 200);
    }
}
