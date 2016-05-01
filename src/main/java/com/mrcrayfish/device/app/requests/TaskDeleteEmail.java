package com.mrcrayfish.device.app.requests;

import java.util.List;

import com.mrcrayfish.device.app.ApplicationEmail.Email;
import com.mrcrayfish.device.app.ApplicationEmail.EmailManager;
import com.mrcrayfish.device.task.Task;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskDeleteEmail extends Task {
	
	private int index;
	
	public TaskDeleteEmail() {}
	
	public TaskDeleteEmail(int index) 
	{
		this.index = index;
	}

	@Override
	public String getName() 
	{
		return "delete_email";
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) 
	{
		nbt.setInteger("Index", this.index);
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		List<Email> emails = EmailManager.INSTANCE.getEmailsForAccount(player);
		if(emails != null)
		{
			int index = nbt.getInteger("Index");
			if(index >= 0 && index < emails.size())
			{
				emails.remove(index);
				this.setSuccessful();
			}
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		nbt.setBoolean("Deleted", this.isSucessful());
	}

	@Override
	public void processResponse(NBTTagCompound nbt)
	{
		if(nbt.getBoolean("Deleted"))
			this.setSuccessful();
	}
}
