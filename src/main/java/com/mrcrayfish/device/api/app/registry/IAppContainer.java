package com.mrcrayfish.device.api.app.registry;

import com.mrcrayfish.device.api.app.Application;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IAppContainer {
    Class<? extends Application> getContainedAppClass();
    ResourceLocation getAppId();
    boolean isDebug();
    boolean isSystemApp();
}
