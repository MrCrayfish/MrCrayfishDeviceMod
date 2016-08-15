package com.mrcrayfish.device.programs.email.tasks;

import com.mrcrayfish.device.api.app.task.Task;
import com.mrcrayfish.device.programs.email.ApplicationEmail.EmailManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskRegisterEmailAccount extends Task
{
	private String name;
	
	public TaskRegisterEmailAccount() 
	{
		super("register_email_account");
	}
	
	public TaskRegisterEmailAccount(String name) 
	{
		this();
		this.name = name;
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) 
	{
		nbt.setString("AccountName", this.name);
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		if(EmailManager.INSTANCE.addAccount(player, nbt.getString("AccountName")))
			this.setSuccessful();
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		nbt.setBoolean("Created", this.isSucessful());
	}

	@Override
	public void processResponse(NBTTagCompound nbt) 
	{
		if(nbt.getBoolean("Created"))
			this.setSuccessful();
	}

}
