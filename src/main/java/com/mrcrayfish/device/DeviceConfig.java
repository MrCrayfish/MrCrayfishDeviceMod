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
    private static Configuration config;

    public static void load(File file)
    {
        config = new Configuration(file);
        init();
    }

    private static void init()
    {
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
}
