package com.mrcrayfish.device.programs.email.task;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.registry.CDMRegister;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.object.Email;
import com.mrcrayfish.device.programs.email.EmailManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

@CDMRegister(modId = Reference.MOD_ID, uid = "email_delete")
public class TaskDeleteEmail extends Task {
	
	private int index;
	
	public TaskDeleteEmail() 
	{
		super("delete_email");
	}
	
	public TaskDeleteEmail(int index) 
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
				emails.remove(index);
				this.setSuccessful();
			}
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) {}

	@Override
	public void processResponse(NBTTagCompound nbt) {}
}
