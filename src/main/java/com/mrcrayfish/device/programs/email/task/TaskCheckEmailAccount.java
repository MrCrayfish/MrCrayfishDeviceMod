package com.mrcrayfish.device.programs.email.task;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.annotation.DeviceTask;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.EmailManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@DeviceTask(modId = Reference.MOD_ID, taskId = "check_email_account")
public class TaskCheckEmailAccount extends Task 
{
	private boolean hasAccount = false;
	private String name = null;

	@Override
	public void prepareRequest(NBTTagCompound nbt) {}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		this.hasAccount = EmailManager.INSTANCE.hasAccount(player.getUniqueID());
		if(this.hasAccount)
		{
			this.name = EmailManager.INSTANCE.getName(player);
			this.setSuccessful();
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		if(this.isSucessful()) nbt.setString("Name", this.name);
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {}

}
