package com.mrcrayfish.device.api.registry;

import com.mrcrayfish.device.api.app.Application;
import net.minecraft.util.ResourceLocation;

public interface BasicContainer<T> extends ICDMRegistryItem<BasicContainer<T>>{
    Class<? extends T> getContainedClass();
    ResourceLocation getId();
    boolean isSystem();
}
