package com.mrcrayfish.device.programs.email.task;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.registry.CDMRegister;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.object.Email;
import com.mrcrayfish.device.programs.email.EmailManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.List;

@CDMRegister(modId = Reference.MOD_ID, uid = "email_update_inbox")
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
		this.emails = EmailManager.INSTANCE.getEmailsForAccount(player);
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
