package com.mrcrayfish.device.app.requests;

import java.util.List;

import com.mrcrayfish.device.app.ApplicationEmail;
import com.mrcrayfish.device.app.ApplicationEmail.Email;
import com.mrcrayfish.device.app.ApplicationEmail.EmailManager;
import com.mrcrayfish.device.task.Task;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class TaskUpdateInbox extends Task
{
	private String email;
	private List<ApplicationEmail.Email> emails;
	
	public TaskUpdateInbox() {}
	
	public TaskUpdateInbox(String email) 
	{
		this.email = email;
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) 
	{
		nbt.setString("email", email);
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		String email = nbt.getString("email");
		System.out.println(email);
		this.emails = EmailManager.getEmailsForAccount(email);
		System.out.println(emails);
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
		EmailManager.inbox.clear();
		NBTTagList emails = (NBTTagList) nbt.getTag("emails");
		for(int i = 0; i < emails.tagCount(); i++)
		{
			NBTTagCompound emailTag = emails.getCompoundTagAt(i);
			Email email = Email.readFromNBT(emailTag);
			EmailManager.inbox.add(email);
			System.out.println("Adding to inbox");
		}
	}

	@Override
	public String getName() 
	{
		return "update_inbox";
	}
}
