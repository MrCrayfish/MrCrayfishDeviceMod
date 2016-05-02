package com.mrcrayfish.device.app.requests;

import java.util.List;

import com.mrcrayfish.device.programs.ApplicationEmail.Email;
import com.mrcrayfish.device.programs.ApplicationEmail.EmailManager;
import com.mrcrayfish.device.task.Task;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskViewEmail extends Task
{
	private int index;
	
	public TaskViewEmail() {}
	
	public TaskViewEmail(int index) 
	{
		this.index = index;
	}

	@Override
	public String getName() 
	{
		return "view_email";
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
				emails.get(index).setRead(true);
			}
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) {}

	@Override
	public void processResponse(NBTTagCompound nbt) {}
	
}
