package com.mrcrayfish.device.api.task;

import java.util.HashMap;
import java.util.Map;

import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.message.MessageRequest;

public class TaskManager 
{
	private static int currentId = 0;
	
	private static Map<String, Task> registeredRequests = new HashMap<String, Task>();
	private static Map<Integer, Task> requests = new HashMap<Integer, Task>();
	
	/**
	 * Registers a task. You must do this otherwise your task will
	 * be rejected if you attempt to send it to the server.
	 * 
	 * @param clazz the class of the Task you want to register
	 */
	public final static void registerRequest(Class clazz)
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
	
	public final static Task getRequest(String name)
	{
		return registeredRequests.get(name);
	}
	
	/**
	 * Sends a Task to the server.
	 * 
	 * @param task
	 */
	public final static void sendRequest(Task task)
	{
		if(!registeredRequests.containsKey(task.getName())) {
			throw new RuntimeException("Unregistered Task: " + task.getClass().getName() + ". Use TaskManager#requestRequest to register your task.");
		}
		
		int requestId = currentId++;
		requests.put(requestId, task);
		PacketHandler.INSTANCE.sendToServer(new MessageRequest(requestId, task));
	}
	
	public final static Task getTaskAndRemove(int id)
	{
		return requests.remove(id);
	}
}
