package com.mrcrayfish.device.api.registry.task;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.registry.BasicContainer;
import com.mrcrayfish.device.api.registry.CDMRegister;
import com.mrcrayfish.device.api.registry.CDMRegistry;
import com.mrcrayfish.device.api.registry.ICDMRegistryItem;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

public class TaskRegistry extends CDMRegistry<BasicContainer<Task>, Task> {

    @Override
    public void addItem(CDMRegister annotation, Class<? extends Task> annotatedClass) {
        final BasicContainer<Task> task = newTaskContainer(annotation, annotatedClass);
        this.registry.add(task);
    }
    
    private static BasicContainer<Task> newTaskContainer(CDMRegister app, Class<? extends Task> clazz){
        return new BasicContainer<Task>() {

            @Override
            public BasicContainer<Task> getType() {
                return this;
            }

            @Override
            public Class<? extends Task> getContainedClass() {
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
    }

    @Override
    public void registerItems(Set<? extends ICDMRegistryItem> items) {
        for(ICDMRegistryItem task : items){
            if(task instanceof BasicContainer){
                BasicContainer<Task> container = (BasicContainer<Task>)task;
                TaskManager.registerTask(container);
            }
        }
    }

    @Override
    public Class<Task> getRequiredSuperclass() {
        return Task.class;
    }

    @Override
    public ResourceLocation getRegistryId() {
        return new ResourceLocation(Reference.MOD_ID, "task_registry");
    }
}
