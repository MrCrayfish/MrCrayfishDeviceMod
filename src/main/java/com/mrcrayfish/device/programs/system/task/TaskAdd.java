package com.mrcrayfish.device.programs.system.task;

import java.util.UUID;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.programs.system.object.Account;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskAdd extends Task 
{
	private int amount;
	
	public TaskAdd()
	{
		super("bank_add");
	}
	
	public TaskAdd(int amount)
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
		int amount = nbt.getInteger("amount");
		Account sender = BankUtil.INSTANCE.getAccount(player);
		sender.add(amount);
		this.setSuccessful();
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		nbt.setInteger("balance", this.amount);
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {}
}
