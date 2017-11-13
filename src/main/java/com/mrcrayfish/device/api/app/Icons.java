package com.mrcrayfish.device.api.app;

import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public enum Icons implements IIcon
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
    INFO,
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
    HAMMER,
    FORBIDDEN,
    MUSIC,
    EYE_DROPPER,
    DOTS_VERTICAL,
    DOTS_HORIZONTAL,
    EXPAND,
    SHRINK,
    SORT,
    FONT,
    ALIGN_LEFT,
    ALIGN_CENTER,
    ALIGN_RIGHT,
    ALIGN_JUSTIFY,
    COIN,
    CASH,
    VERIFIED,
    BOOK_CLOSED,
    BOOK_OPEN,
    VIDEO_ROLL,
    VIDEO_CAMERA,
    LIGHT_BULB_OFF,
    LIGHT_BULB_ON,
    LOCATION,
    SEND,
    LOGIN,
    LOGOUT,
    HELP,
    HEART_OFF,
    HEART_ON,
    MAP,
    BRIGHTNESS;

    private static final ResourceLocation ICON_ASSET = new ResourceLocation("cdm:textures/gui/icons.png");

    private static final int ICON_SIZE = 10;
    private static final int GRID_SIZE = 20;

	@Override
	public ResourceLocation getIconAsset()
    {
        return ICON_ASSET;
    }

	@Override
	public int getIconSize()
    {
		return ICON_SIZE;
	}

    @Override
	public int getGridWidth()
    {
        return GRID_SIZE;
    }

    @Override
    public int getGridHeight()
    {
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
}
