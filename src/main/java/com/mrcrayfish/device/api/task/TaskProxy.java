package com.mrcrayfish.device.api.task;

import com.mrcrayfish.device.network.task.TaskManager;

public class TaskProxy
{
	private static TaskManager INSTANCE;
	
	/**
	 * Sets the TaskManager instance. Do not use. This is for the core only.
	 * 
	 * @param manager the taks manager instance
	 */
	public static void setInstance(TaskManager manager) 
	{
		if(INSTANCE == null && manager != null)
		{
			INSTANCE = manager;
		}
	}
	
	/**
	 * Registers a task. You must do this otherwise your task will
	 * be rejected if you attempt to send it to the server.
	 * 
	 * @param task the class of the Task you want to register
	 */
	public static void registerTask(Class clazz)
	{
		INSTANCE.registerTask(clazz);
	}
	
	/**
	 * Sends a Task to the server.
	 * 
	 * @param task
	 */
	public static void sendTask(Task task)
	{
		INSTANCE.sendTask(task);
	}
}
