package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.Bank;
import com.mrcrayfish.device.programs.system.object.Account;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskPay extends Task 
{
	private String uuid;
	private int amount;
	
	public TaskPay()
	{
		super("bank_pay");
	}
	
	public TaskPay(String uuid, int amount)
	{
		this();
		this.amount = amount;
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt)
	{
		nbt.setString("player", this.uuid);
		nbt.setInteger("amount", this.amount);
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
	{
		String uuid = nbt.getString("uuid");
		int amount = nbt.getInteger("amount");
		Account sender = Bank.INSTANCE.getAccount(player);
		Account recipient = Bank.INSTANCE.getAccount(uuid);
		if(recipient != null && sender.hasAmount(amount)) {
			recipient.add(amount);
			sender.remove(amount);
			this.amount = sender.getBalance();
			this.setSuccessful();
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		nbt.setInteger("balance", this.amount);
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {}
}
