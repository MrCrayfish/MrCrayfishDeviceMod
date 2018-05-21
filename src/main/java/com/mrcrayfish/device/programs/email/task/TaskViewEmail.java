package com.mrcrayfish.device.programs.email.task;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.annotation.DeviceTask;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.object.Email;
import com.mrcrayfish.device.programs.email.EmailManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

@DeviceTask(modId = Reference.MOD_ID, taskId = "view_email")
public class TaskViewEmail extends Task
{
	private int index;
	
	public TaskViewEmail() {}
	
	public TaskViewEmail(int index) 
	{
		this();
		this.index = index;
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
