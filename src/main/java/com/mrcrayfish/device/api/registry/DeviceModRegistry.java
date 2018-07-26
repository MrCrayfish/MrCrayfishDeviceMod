package com.mrcrayfish.device.api.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DeviceModRegistry {
    private static final Map<ResourceLocation, CDMRegistry<? extends ICDMRegistryItem<?>, ?>> REGISTRIES = new HashMap<>();

    public static void addCDMRegistry(Class<? extends CDMRegistry<? extends ICDMRegistryItem<?>, ?>> cls){
        try {
            final CDMRegistry<? extends ICDMRegistryItem<?>, ?> instance = cls.newInstance();
            REGISTRIES.put(instance.getRegistryId(), instance);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void startRegistries(ASMDataTable dataTable){
        for(CDMRegistry<? extends ICDMRegistryItem, ?> reg : REGISTRIES.values()){
            reg.populate(dataTable);
            reg.registerItems(reg.registry);
        }
    }

    @Nullable
    public static CDMRegistry<? extends ICDMRegistryItem<?>, ?> getRegistry(String name){
        final Optional optional = REGISTRIES.values().stream().filter(it -> it.getRegistryId().getResourcePath().equals(name)).findFirst();
        if(optional.isPresent()){
            return (CDMRegistry<? extends ICDMRegistryItem<?>, ?>) optional.get();
        }
        return null;
    }
}
