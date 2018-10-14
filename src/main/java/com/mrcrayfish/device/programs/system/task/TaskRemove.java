package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.annotation.DeviceTask;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.programs.system.object.Account;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@DeviceTask(modId = Reference.MOD_ID, taskId = "bank_remove")
public class TaskRemove extends Task 
{
	private int amount;
	
	public TaskRemove() {}
	
	public TaskRemove(int amount)
	{
		this();
		this.amount = amount;
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt)
	{
		nbt.setInteger("amount", this.amount);
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
	{
		this.amount = nbt.getInteger("amount");
		Account sender = BankUtil.INSTANCE.getAccount(player);
		if(sender.hasAmount(amount))
		{
			sender.remove(amount);
			this.setSuccessful();
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		if(isSucessful())
		{
			nbt.setInteger("balance", this.amount);
		}
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {}
}
