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
	
	public final void registerTask(Class<? extends Task> clazz)
	{
		try 
		{
			Constructor[] constructors = clazz.getDeclaredConstructors();
			for(Constructor constructor : constructors)
			{
				if(constructor.getParameterCount() == 0)
				{
					constructor.setAccessible(true);
					Task task = (Task) constructor.newInstance();
					System.out.println("Registering task '" + task.getName() + "'");
					registeredRequests.put(task.getName(), task);
					return;
				}
			}
			throw new InstantiationException();
		} 
		catch (InstantiationException e)
		{
			System.err.println("- Missing constructor '" + clazz.getSimpleName() + "()'");
		} 
		catch (IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	public final void sendTask(Task task)
	{
		if(!registeredRequests.containsKey(task.getName())) {
			throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#registerTask to register your task.");
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
