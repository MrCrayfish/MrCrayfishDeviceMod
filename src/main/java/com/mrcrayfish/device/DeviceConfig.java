package com.mrcrayfish.device;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;


/**
 * Author: MrCrayfish
 */
public class DeviceConfig
{
    private static final String CATEGORY_PRINTING = "printer-settings";
    private static boolean overridePrintSpeed;
    private static int customPrintSpeed;
    private static int maxPaperCount;

    private static final String CATEGORY_PIXEL_PAINTER = "pixel-painter";
    private static boolean pixelPainterEnable;
    private static boolean renderPrinted3D;

    private static Configuration config;

    public static void load(File file)
    {
        config = new Configuration(file);
        init();
    }

    private static void init()
    {
        overridePrintSpeed = config.get(CATEGORY_PRINTING, "overridePrintSpeed", false, "If enable, overrides all printing times with customPrintSpeed property").getBoolean();
        customPrintSpeed = config.get(CATEGORY_PRINTING, "customPrintSpeed", 20, "The amount of seconds it should take for the printer to addToQueue a document", 1, 600).getInt();
        maxPaperCount = config.get(CATEGORY_PRINTING, "maxPaperCount", 64, "The amount of paper that can be loaded into the printer", 0, 99).getInt();

        pixelPainterEnable = config.get(CATEGORY_PIXEL_PAINTER, "enabled", true, "Enable or disable this app").getBoolean();
        renderPrinted3D = config.get(CATEGORY_PIXEL_PAINTER, "render-printed-in-3d", false, "Should the pixels on printed pictures render in 3D. Warning, this will decrease the performance of the game. You should not enable if you have a slow computer!").getBoolean();

        config.save();
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.getModID().equals(Reference.MOD_ID))
        {
            init();
        }
    }

    public static boolean isOverridePrintSpeed()
    {
        return overridePrintSpeed;
    }

    public static int getCustomPrintSpeed()
    {
        return customPrintSpeed;
    }

    public static int getMaxPaperCount()
    {
        return maxPaperCount;
    }

    public static boolean isPixelPainterEnable()
    {
        return pixelPainterEnable;
    }

    public static boolean isRenderPrinted3D()
    {
        return renderPrinted3D;
    }
}
