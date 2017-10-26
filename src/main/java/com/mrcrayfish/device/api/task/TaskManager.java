package com.mrcrayfish.device.api.task;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.task.MessageRequest;

public final class TaskManager
{
	private static TaskManager instance = null;

	private Map<String, Task> registeredRequests = new HashMap<String, Task>();
	private Map<Integer, Task> requests = new HashMap<Integer, Task>();
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
	
	public static void registerTask(Class<? extends Task> clazz)
	{
		try 
		{
			Constructor<? extends Task> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			Task task = constructor.newInstance();
			MrCrayfishDeviceMod.getLogger().info("Registering task '" + task.getName() + "'");
			get().registeredRequests.put(task.getName(), task);
		} 
		catch (InstantiationException e) 
		{	
			System.err.println("- Missing constructor '" + clazz.getSimpleName() + "()'");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void sendTask(Task task)
	{
		TaskManager manager = get();
		if(!manager.registeredRequests.containsKey(task.getName())) {
			throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#requestRequest to register your task.");
		}
		
		int requestId = manager.currentId++;
		manager.requests.put(requestId, task);
		PacketHandler.INSTANCE.sendToServer(new MessageRequest(requestId, task));
	}
	
	public static Task getTask(String name)
	{
		return get().registeredRequests.get(name);
	}

	public static Task getTaskAndRemove(int id)
	{
		return get().requests.remove(id);
	}
}
