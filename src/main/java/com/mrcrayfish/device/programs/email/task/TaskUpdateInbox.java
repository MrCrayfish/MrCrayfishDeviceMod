package com.mrcrayfish.device.programs.email.task;

import java.util.List;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.ApplicationEmail;
import com.mrcrayfish.device.programs.email.EmailManager;
import com.mrcrayfish.device.programs.email.object.Email;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class TaskUpdateInbox extends Task
{
	private List<Email> emails;
	
	public TaskUpdateInbox() 
	{
		super("update_inbox");
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) {}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		this.emails = EmailManager.INSTANCE.getInbox(player);
		this.setSuccessful();
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		NBTTagList tagList = new NBTTagList();
		if(emails != null)
		{
			for(Email email : emails)
			{
				NBTTagCompound emailTag = new NBTTagCompound();
				email.writeToNBT(emailTag);
				tagList.appendTag(emailTag);
			}
		}
		nbt.setTag("emails", tagList);
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {}
}
