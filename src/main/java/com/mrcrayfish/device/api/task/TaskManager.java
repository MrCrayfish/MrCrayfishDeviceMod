package com.mrcrayfish.device.api.task;

import com.google.common.collect.HashBiMap;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.annotation.DeviceTask;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.task.MessageRequest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TaskManager
{
	private static TaskManager instance = null;

	private final HashBiMap<String, Class<? extends Task>> registeredTasks = HashBiMap.create();
	private final Map<String, Task> instances = new HashMap<>();
	private final Map<Integer, Task> pendingTasks = new HashMap<Integer, Task>();
	private int currentId = 0;

	private TaskManager() {}

	private static TaskManager get()
	{
		if(instance == null)
		{
			instance = new TaskManager();
		}
		return instance;
	}

	public static void init(ASMDataTable table)
	{
		Set<ASMDataTable.ASMData> tasks = table.getAll(DeviceTask.class.getCanonicalName());
		tasks.forEach(asmData ->
		{
			try
			{
				Class clazz = Class.forName(asmData.getClassName());
				if(Task.class.isAssignableFrom(clazz))
				{
					Constructor<?> constructor = getEmptyConstructor(clazz);
					if(constructor == null)
					{
						throw new RuntimeException("The task " + clazz.getCanonicalName() + " is missing the constructor " + clazz.getSimpleName() + "()");
					}
					constructor.setAccessible(true);

					DeviceTask deviceApp = (DeviceTask) clazz.getDeclaredAnnotation(DeviceTask.class);
					Task task = (Task) constructor.newInstance();
					ResourceLocation resource = new ResourceLocation(deviceApp.modId(), deviceApp.taskId());
					get().registeredTasks.put(resource.toString(), clazz);
					get().instances.put(resource.toString(), task);

					MrCrayfishDeviceMod.getLogger().info("Successfully registered task " + deviceApp.modId() + ":" + deviceApp.taskId());
				}
				else
				{
					throw new ClassCastException("The class " + clazz.getCanonicalName() + " is annotated with DeviceApplication but does not extend Application!");
				}
			}
			catch(ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
		});
	}

	private static Constructor getEmptyConstructor(Class clazz)
	{
		for (Constructor<?> constructor : clazz.getDeclaredConstructors())
		{
			if (constructor.getParameterCount() == 0)
			{
				return constructor;
			}
		}
		return null;
	}

	public static void sendTask(Task task)
	{
		TaskManager manager = get();
		if(!manager.registeredTasks.values().contains(task.getClass())) {
			throw new RuntimeException("Unregistered task '" + task.getClass().getName() + "'");
		}

		int requestId = manager.currentId++;
		manager.pendingTasks.put(requestId, task);
		PacketHandler.INSTANCE.sendToServer(new MessageRequest(requestId, task));
	}
	
	public static Task getTask(String name)
	{
		return get().instances.get(name);
	}

	public static Task getTaskAndRemove(int id)
	{
		return get().pendingTasks.remove(id);
	}

	public static String getTaskName(Task task)
	{
		return get().registeredTasks.inverse().get(task.getClass());
	}
}
