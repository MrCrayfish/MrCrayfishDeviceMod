package com.mrcrayfish.device.init;

import com.mrcrayfish.device.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class DeviceSounds
{
    public static final SoundEvent PRINTER_PRINTING;
    public static final SoundEvent PRINTER_LOADING_PAPER;

    static
    {
        PRINTER_PRINTING = registerSound("cdm:printing_ink");
        PRINTER_LOADING_PAPER = registerSound("cdm:printing_paper");
    }

    private static SoundEvent registerSound(String soundNameIn)
    {
        ResourceLocation resource = new ResourceLocation(soundNameIn);
        SoundEvent sound = new SoundEvent(resource).setRegistryName(soundNameIn);
        return sound;
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerSounds(final RegistryEvent.Register<SoundEvent> event)
        {
            event.getRegistry().register(PRINTER_PRINTING);
            event.getRegistry().register(PRINTER_LOADING_PAPER);
        }
    }
}
