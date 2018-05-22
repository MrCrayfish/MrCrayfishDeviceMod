package com.mrcrayfish.device.api.app.registry;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.annotation.Annotation;
import java.util.*;

public class ApplicationRegistry {
    private static final Set<IAppContainer> apps = new HashSet<>();

    public static void populateApps(ASMDataTable asmDataTable){
        MrCrayfishDeviceMod.getLogger().info("Populating annotated apps...");
        final Set<ASMDataTable.ASMData> rawapps = asmDataTable.getAll(App.class.getName());
        for(ASMDataTable.ASMData it : rawapps){
            try {
                final String name = it.getClassName();
                final Class<?> clazz = Class.forName(name);
                if(clazz.getSuperclass() == Application.class){
                    final Class<? extends Application> claz = clazz.asSubclass(Application.class);
                    final Annotation[] anns = claz.getAnnotations();
                    for(Annotation a : anns){
                        final String n = a.annotationType().getCanonicalName();
                        if(n.equals(App.class.getCanonicalName())){
                            final App app = (App)a;
                            apps.add(newAppContainer(app, claz));
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        MrCrayfishDeviceMod.getLogger().info("\tDone!");
    }

    private static IAppContainer newAppContainer(App app, Class<? extends Application> clazz){
        MrCrayfishDeviceMod.getLogger().info("Constructing new app container...");
        final IAppContainer ret = new IAppContainer() {
            @Override
            public Class<? extends Application> getContainedAppClass() {
                return clazz;
            }

            @Override
            public ResourceLocation getAppId() {
                return new ResourceLocation(app.modId(), app.appId());
            }

            @Override
            public boolean isDebug() {
                return app.isDebug();
            }

            @Override
            public boolean isSystemApp(){
                return app.isSystemApp();
            }
        };
        MrCrayfishDeviceMod.getLogger().info("\tDone!");
        return ret;
    }

    public static void registerApps(){
        MrCrayfishDeviceMod.getLogger().info("Registering annotated apps...");
        for(IAppContainer app : apps){
            MrCrayfishDeviceMod.getLogger().info(app.getAppId());
            ApplicationManager.registerApplication(app);
        }
    }
}
