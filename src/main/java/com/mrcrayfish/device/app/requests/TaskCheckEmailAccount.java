package com.mrcrayfish.device.app.requests;

import com.mrcrayfish.device.programs.ApplicationEmail.EmailManager;
import com.mrcrayfish.device.task.Task;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskCheckEmailAccount extends Task 
{
	private boolean hasAccount = false;
	private String name = null;
	
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
		this.hasAccount = EmailManager.INSTANCE.hasAccount(player.getUniqueID());
		if(this.hasAccount) this.name = EmailManager.INSTANCE.getName(player);
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		nbt.setBoolean("hasAccount", this.hasAccount);
		if(this.hasAccount) nbt.setString("Name", this.name);
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
