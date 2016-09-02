package com.mrcrayfish.device.programs.system.task;

import java.util.UUID;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
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
		this.uuid = uuid;
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
		Account sender = BankUtil.INSTANCE.getAccount(player);
		Account recipient = BankUtil.INSTANCE.getAccount(UUID.fromString(uuid));
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
		if(isSucessful())
		{
			nbt.setInteger("balance", this.amount);
		}
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {}
}
