package com.mrcrayfish.device.app.requests;

import com.mrcrayfish.device.app.ApplicationEmail.EmailManager;
import com.mrcrayfish.device.task.Task;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskCheckEmailAccount extends Task 
{
	private boolean hasAccount = false;
	
	@Override
	public String getName() 
	{
		return "check_email_account";
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) {}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		this.hasAccount = EmailManager.hasAccount(player.getUniqueID());
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		nbt.setBoolean("hasAccount", this.hasAccount);
	}

	@Override
	public void processResponse(NBTTagCompound nbt) 
	{
		if(nbt.getBoolean("hasAccount"))
		{
			this.setSuccessful();
		}
	}

}
