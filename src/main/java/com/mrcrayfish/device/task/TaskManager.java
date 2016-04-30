package com.mrcrayfish.device.task;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.message.MessageRequest;

public class TaskManager 
{
	public static int currentId = 0;
	
	private static Map<String, Task> registeredRequests = new HashMap<String, Task>();
	private static Map<Integer, Task> requests = new HashMap<Integer, Task>();
	
	public static void registerRequest(Class clazz)
	{
		try 
		{
			Task task = (Task) clazz.newInstance();
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
	}
	
	public static Task getRequest(String name)
	{
		return registeredRequests.get(name);
	}
	
	public static void sendRequest(Task request)
	{
		int requestId = currentId++;
		requests.put(requestId, request);
		PacketHandler.INSTANCE.sendToServer(new MessageRequest(requestId, request));
	}
	
	public static Task getTask(int id)
	{
		return requests.remove(id);
	}
}
