package com.mrcrayfish.device;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;


/**
 * Author: MrCrayfish
 */
public class DeviceConfig
{
    private static final String CATEGORY_LAPTOP = "laptop-settings";
    private static int pingRate;

    private static final String CATEGORY_ROUTER = "router-settings";
    private static int signalRange;
    private static int beaconInterval;
    private static int maxDevices;

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
        pingRate = config.get(CATEGORY_LAPTOP, "pingRate", 20, "The amount of ticks the laptop waits until sending another ping to it's connected router.", 1, 200).getInt();

        signalRange = config.get(CATEGORY_ROUTER, "signalRange", 20, "The range that routers can produce a signal to devices. This is the radius in blocks. Be careful when increasing this value, the performance is O(n^3) and larger numbers will have a bigger impact on the server", 10, 100).getInt();
        beaconInterval = config.get(CATEGORY_ROUTER, "beaconInterval", 20, "The amount of ticks the router waits before sending out a beacon signal. Higher number will increase performance but devices won't know as quick if they lost connection.", 1, 200).getInt();
        maxDevices = config.get(CATEGORY_ROUTER, "maxDevices", 16, "The maximum amount of devices that can be connected to the router.", 1, 64).getInt();

        overridePrintSpeed = config.get(CATEGORY_PRINTING, "overridePrintSpeed", false, "If enable, overrides all printing times with customPrintSpeed property").getBoolean();
        customPrintSpeed = config.get(CATEGORY_PRINTING, "customPrintSpeed", 20, "The amount of seconds it should take for the printer to addToQueue a document", 1, 600).getInt();
        maxPaperCount = config.get(CATEGORY_PRINTING, "maxPaperCount", 64, "The amount of paper that can be loaded into the printer", 0, 99).getInt();

        pixelPainterEnable = config.get(CATEGORY_PIXEL_PAINTER, "enabled", true, "Enable or disable this app").getBoolean();
        renderPrinted3D = config.get(CATEGORY_PIXEL_PAINTER, "render-printed-in-3d", false, "Should the pixels on printed pictures render in 3D. Warning, this will decrease the performance of the game. You should not enable if you have a slow computer!").getBoolean();

        config.save();
    }

    public static void readSyncTag(NBTTagCompound tag)
    {
        if(tag.hasKey("pingRate", Constants.NBT.TAG_INT))
        {
            pingRate = tag.getInteger("pingRate");
        }
        if(tag.hasKey("signalRange", Constants.NBT.TAG_INT))
        {
            signalRange = tag.getInteger("signalRange");
        }
    }

    public static NBTTagCompound writeSyncTag()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("pingRate", pingRate);
        tag.setInteger("signalRange", signalRange);
        return tag;
    }

    public static void restore()
    {
        init();
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.getModID().equals(Reference.MOD_ID))
        {
            init();
        }
    }

    public static int getPingRate()
    {
        return pingRate;
    }

    public static int getSignalRange()
    {
        return signalRange;
    }

    public static int getBeaconInterval()
    {
        return beaconInterval;
    }

    public static int getMaxDevices()
    {
        return maxDevices;
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
