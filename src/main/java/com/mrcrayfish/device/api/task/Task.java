package com.mrcrayfish.device.api.task;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * <p>A Task is simple implementation that allows you to make calls to the
 * server to process actions, store or retrieve data, etc. Useful for any
 * client-server like applications, e.g. Emails, Instant Messaging, etc</p> 
 * 
 * <p>Any global variables that are initialized in this class, wont be on the server side.
 * To initialize them, first store the data in the NBT tag provided in {@link #prepareRequest(NBTTagCompound)},
 * then once your Task gets to the server, use {@link #processRequest(NBTTagCompound, World, EntityPlayer)} to
 * get the data from the NBT tag parameter. Initialize the variables as normal.
 * 
 * <p>Please check out the example applications to get a better understanding 
 * how this could be useful to your application.</p>
 * 
 * @author MrCrayfish
 */
public abstract class Task 
{
	private String name;
	private Callback<NBTTagCompound> callback = null;
	private boolean success = false;
	
	public Task(String name)
	{
		this.name = name;
	}
	
	/**
	 * Sets the callback for task. Used for processing responses, 
	 * such as updating UI with new data.
	 * 
	 * @param callback the callback instance for response processing
	 * 
	 * @return this Task instance
	 */
	public final Task setCallback(Callback<NBTTagCompound> callback)
	{
		this.callback = callback;
		return this;
	}
	
	/**
	 * Runs the callback
	 * 
	 * @param nbt the response data
	 */
	public final void callback(NBTTagCompound nbt)
	{
		if(callback != null)
		{
			callback.execute(nbt, success);
		}
	}
	
	/**
	 * Sets that this Task was successful. Should be called
	 * if your Task produced the correct results, preferably in
	 * {@link #processRequest(NBTTagCompound, World, EntityPlayer)}
	 */
	public final void setSuccessful()
	{
		this.success = true;
	}
	
	/**
	 * Gets if this Task produced the correct results.
	 * 
	 * @return if this task was successful
	 */
	public final boolean isSucessful()
	{
		return this.success;
	}
	
	/**
	 * Sets the task as complete and resets success to false.
	 * This is used for the core.
	 */
	public final void complete()
	{
		this.success = false;
	}
	
	/**
	 * Gets the name of the Task
	 * 
	 * @return the Task name
	 */
	public final String getName() 
	{
		return this.name;
	}
	
	/**
	 * Called before the request is sent off to the server. 
	 * You should store the data you want to sendTask into the NBT Tag
	 * 
	 * @param nbt The NBT to be sent to the server
	 */
	public abstract void prepareRequest(NBTTagCompound nbt);
	
	/**
	 * Called when the request arrives to the server. Here you can perform actions
	 * with your request. Data attached to the NBT from {@link Task#prepareRequest(NBTTagCompound nbt)}
	 * can be accessed from the NBT tag parameter.
	 * 
	 * @param nbt The NBT Tag received from the client
	 */
	public abstract void processRequest(NBTTagCompound nbt, World world, EntityPlayer player);
	
	/**
	 * Called before the response is sent back to the client. 
	 * You should store the data you want to sendTask back into the NBT Tag
	 * 
	 * @param nbt The NBT to be sent back to the client
	 */
	public abstract void prepareResponse(NBTTagCompound nbt);
	
	/**
	 * Called when the response arrives to the client. Here you can update data
	 * on the client side. If you want to update any UI component, you should set
	 * a Callback before you sendTask the request. See {@link #setCallback(Callback)}
	 * 
	 * @param nbt The NBT Tag received from the server
	 */
	public abstract void processResponse(NBTTagCompound nbt);
}
