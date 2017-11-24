package com.mrcrayfish.device.api.print;

import com.google.common.collect.HashBiMap;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class PrintingManager
{
    private static HashBiMap<String, Class<? extends IPrint>> registeredPrints = HashBiMap.create();

    @SideOnly(Side.CLIENT)
    private static Map<String, IPrint.Renderer> registeredRenders = new HashMap<>();

    public static void registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint)
    {
        try
        {
            classPrint.getConstructor().newInstance();
            if(MrCrayfishDeviceMod.proxy.registerPrint(identifier, classPrint))
            {
                registeredPrints.put(identifier.toString(), classPrint);
            }
            else
            {
                MrCrayfishDeviceMod.getLogger().error("The print '" + classPrint.getName() + "' could not be registered due to a critical error!");
            }
        }
        catch(NoSuchMethodException e)
        {
            MrCrayfishDeviceMod.getLogger().error("The print '" + classPrint.getName() + "' is missing an empty constructor and could not be registered!");
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            MrCrayfishDeviceMod.getLogger().error("The print '" + classPrint.getName() + "' could not be registered due to a critical error!");
        }
    }

    public static boolean isRegisteredPrint(Class<? extends IPrint> clazz)
    {
        return registeredPrints.containsValue(clazz);
    }

    @SideOnly(Side.CLIENT)
    public static IPrint.Renderer getRenderer(String identifier)
    {
        return registeredRenders.get(identifier);
    }

    public static String getPrintIdentifier(IPrint print)
    {
        return registeredPrints.inverse().get(print.getClass());
    }
}
