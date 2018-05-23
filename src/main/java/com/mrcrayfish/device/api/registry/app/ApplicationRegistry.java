package com.mrcrayfish.device.api.registry.app;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.registry.BasicContainer;
import com.mrcrayfish.device.api.registry.CDMRegister;
import com.mrcrayfish.device.api.registry.CDMRegistry;
import com.mrcrayfish.device.api.registry.ICDMRegistryItem;
import net.minecraft.util.ResourceLocation;
import java.util.*;

public class ApplicationRegistry extends CDMRegistry<BasicContainer<Application>, Application> {

    @Override
    public void addItem(CDMRegister item, Class<? extends Application> annotatedClass) {
        final BasicContainer<Application> app = newAppContainer(item, annotatedClass);
        this.registry.add(app);
    }

    @Override
    public Class<Application> getRequiredSuperclass() {
        return Application.class;
    }

    @Override
    public void registerItems(Set<? extends ICDMRegistryItem> items) {
        for(ICDMRegistryItem item : items){
            if(item.getType() instanceof BasicContainer){
                BasicContainer app = (BasicContainer)item.getType();
                ApplicationManager.registerApplication(app);
            }
        }
    }

    private static BasicContainer<Application> newAppContainer(CDMRegister app, Class<? extends Application> clazz){
        MrCrayfishDeviceMod.getLogger().info("Constructing new app container...");
        final BasicContainer<Application> ret = new BasicContainer<Application>(){

            @Override
            public BasicContainer<Application> getType() {
                return this;
            }

            @Override
            public Class<? extends Application> getContainedClass() {
                return clazz;
            }

            @Override
            public ResourceLocation getId() {
                return new ResourceLocation(app.modId(), app.uid());
            }

            @Override
            public boolean isSystem() {
                return app.isSystem();
            }
        };
        MrCrayfishDeviceMod.getLogger().info("\tDone!");
        return ret;
    }

    @Override
    public ResourceLocation getRegistryId() {
        return new ResourceLocation(Reference.MOD_ID, "apps_registry");
    }
}
