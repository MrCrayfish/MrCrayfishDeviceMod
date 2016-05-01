package com.mrcrayfish.device.app.requests;

import com.mrcrayfish.device.app.ApplicationEmail.EmailManager;
import com.mrcrayfish.device.task.Task;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskRegisterEmailAccount extends Task
{
	private String name;
	
	public TaskRegisterEmailAccount() {}
	
	public TaskRegisterEmailAccount(String name) 
	{
		this.name = name;
	}
	
	@Override
	public String getName() 
	{
		return "register_email_account";
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
