package com.mrcrayfish.device.app.requests;

import com.mrcrayfish.device.app.ApplicationEmail.Email;
import com.mrcrayfish.device.app.ApplicationEmail.EmailManager;
import com.mrcrayfish.device.task.Task;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskSendEmail extends Task 
{
	private Email email;
	private String to;
	
	public TaskSendEmail() {}
	
	public TaskSendEmail(Email email, String to)
	{
		this.email = email;
		this.to = to;
	}
	
	@Override
	public String getName() 
	{
		return "send_email";
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) 
	{
		this.email.writeToNBT(nbt);
		nbt.setString("to", this.to);
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		String name = EmailManager.getName(player);
		if(name != null)
		{
			Email email = Email.readFromNBT(nbt);
			email.setAuthor(name);
			if(EmailManager.addEmailToInbox(email, nbt.getString("to"))) 
			{
				this.setSuccessful();
			}
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		nbt.setBoolean("Sent", this.isSucessful());
	}

	@Override
	public void processResponse(NBTTagCompound nbt) 
	{
		if(nbt.getBoolean("Sent")) 
			this.setSuccessful();
	}

}
