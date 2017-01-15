package com.mrcrayfish.device.programs.email.task;

import java.util.List;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.ApplicationEmail;
import com.mrcrayfish.device.programs.email.ApplicationEmail.Email;
import com.mrcrayfish.device.programs.email.ApplicationEmail.EmailManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class TaskUpdateInbox extends Task
{
	private List<ApplicationEmail.Email> emails;
	
	public TaskUpdateInbox() 
	{
		super("update_inbox");
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) {}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		this.emails = EmailManager.INSTANCE.getEmailsForAccount(player);
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		NBTTagList tagList = new NBTTagList();
		if(emails != null)
		{
			for(ApplicationEmail.Email email : emails)
			{
				NBTTagCompound emailTag = new NBTTagCompound();
				email.writeToNBT(emailTag);
				tagList.appendTag(emailTag);
			}
		}
		nbt.setTag("emails", tagList);
	}

	@Override
	public void processResponse(NBTTagCompound nbt) 
	{
		EmailManager.INSTANCE.getInbox().clear();
		NBTTagList emails = (NBTTagList) nbt.getTag("emails");
		for(int i = 0; i < emails.tagCount(); i++)
		{
			NBTTagCompound emailTag = emails.getCompoundTagAt(i);
			Email email = Email.readFromNBT(emailTag);
			EmailManager.INSTANCE.getInbox().add(email);
		}
	}
}
