package com.mrcrayfish.device.api.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public abstract class CDMRegistry<T extends ICDMRegistryItem<T>, R> {
    public Set<T> registry = new HashSet<>();

    public void populate(ASMDataTable dataTable){
        Set<ASMDataTable.ASMData> anns = dataTable.getAll(CDMRegister.class.getCanonicalName());
        for(ASMDataTable.ASMData data : anns){
            try {
                final String name = data.getClassName();
                final Class<?> clazz = Class.forName(name);
                for(Annotation a : clazz.getDeclaredAnnotations()){
                    final String n = a.annotationType().getCanonicalName();
                    if(n.equals(CDMRegister.class.getCanonicalName())){
                        final CDMRegister ann = (CDMRegister)a;
                        if(getRequiredSuperclass().isAssignableFrom(clazz)){
                            final Class<? extends R> cls = clazz.asSubclass(getRequiredSuperclass());
                            addItem(ann, cls);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void addItem(CDMRegister annotation, Class<? extends R> annotatedClass);
    public abstract void registerItems(Set<? extends ICDMRegistryItem> items);
    public abstract Class<R> getRequiredSuperclass();
    public abstract ResourceLocation getRegistryId();
}
