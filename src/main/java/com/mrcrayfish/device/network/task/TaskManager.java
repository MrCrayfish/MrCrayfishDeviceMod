package com.mrcrayfish.device.network.task;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.network.PacketHandler;

public class TaskManager 
{
	private static Map<String, Task> registeredRequests = new HashMap<String, Task>();
	private static Map<Integer, Task> requests = new HashMap<Integer, Task>();
	
	private static int currentId = 0;
	
	private TaskManager() {}
	
	public final void registerTask(Class clazz)
	{
		try 
		{
			Constructor<Task> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			Task task = (Task) constructor.newInstance();
			System.out.println("Registering task '" + task.getName() + "'");
			registeredRequests.put(task.getName(), task);
		} 
		catch (InstantiationException e) 
		{	
			System.err.println("- Missing constructor '" + clazz.getSimpleName() + "()'");
		} 
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	public final void sendTask(Task task)
	{
		if(!registeredRequests.containsKey(task.getName())) {
			throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#requestRequest to register your task.");
		}
		
		int requestId = currentId++;
		requests.put(requestId, task);
		PacketHandler.INSTANCE.sendToServer(new MessageRequest(requestId, task));
	}
	
	static Task getTask(String name)
	{
		return registeredRequests.get(name);
	}
	
	static Task getTaskAndRemove(int id)
	{
		return requests.remove(id);
	}
}
