package com.mrcrayfish.device.api.print;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class PrintingManager
{
    private static Map<String, IPrint> printTypes = new HashMap<>();

    public static void registerPrint(ResourceLocation identifier, IPrint print)
    {
        if(!printTypes.containsKey(identifier.toString()))
        {
           printTypes.put(identifier.toString(), print);
        }
    }

    public static IPrint getPrint(String identifer)
    {
        return printTypes.get(identifer);
    }
}
