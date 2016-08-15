package com.mrcrayfish.device.api.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.util.InventoryUtil;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class Bank
{
	public static final Bank INSTANCE = new Bank();
	
	private Map<UUID, Account> uuidToAccount = new HashMap<UUID, Account>();
	
	private Bank() {}
	
	public static void deposit(int amount)
	{
		deposit(amount, null);
	}
	
	public static void deposit(int amount, Callback callback) 
	{
		TaskManager.sendRequest(new TaskDeposit().setCallback(callback));
	}
	
	public static void withdraw(int amount)
	{
		withdraw(amount, null);
	}
	
	public static void withdraw(int amount, Callback callback) 
	{
		TaskManager.sendRequest(new TaskWithdraw().setCallback(callback));
	}
	
	public void has(int amount, Callback callback) 
	{
		TaskManager.sendRequest(new TaskWithdraw().setCallback(callback));
	}
	
	public void remove(int amount)
	{
		
	}
	
	public Account getAccount(EntityPlayer player)
	{
		if(!uuidToAccount.containsKey(player.getUniqueID()))
		{
			uuidToAccount.put(player.getUniqueID(), new Account());
		}
		return uuidToAccount.get(player.getUniqueID());
	}
	
	private static class Account 
	{
		private int amount;
		
		private Account() {}
		
		public int getAmount()
		{
			return amount;
		}
		
		public boolean hasAmount(int amount)
		{
			return amount >= this.amount;
		}
		
		public boolean deposit(int amount)
		{
			if(amount > 0)
			{
				this.amount += amount;
				return true;
			}
			return false;
		}
		
		public boolean withdraw(int amount)
		{
			if(hasAmount(amount))
			{
				this.amount -= amount;
				return true;
			}
			return false;
		}
	}
	
	private static class TaskDeposit extends Task 
	{
		private int amount;
		
		public TaskDeposit()
		{
			super("bank_deposit");
		}
		
		public TaskDeposit(int amount)
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
			if(InventoryUtil.removeItemWithAmount(player, Items.emerald, amount))
			{
				Account account = Bank.INSTANCE.getAccount(player);
				if(account.deposit(amount))
				{
					this.setSuccessful();
				}
			}
		}

		@Override
		public void prepareResponse(NBTTagCompound nbt) {}

		@Override
		public void processResponse(NBTTagCompound nbt) {}
	}
	
	private static class TaskWithdraw extends Task 
	{
		private int amount;
		
		public TaskWithdraw()
		{
			super("bank_withdraw");
		}
		
		public TaskWithdraw(int amount)
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
			Account account = Bank.INSTANCE.getAccount(player);
			if(account.withdraw(amount))
			{
				int stacks = amount / 64;
				for(int i = 0; i < stacks; i++)
				{
					world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(Items.emerald, 64)));
				}
				
				int remaining = amount % 64;
				world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(Items.emerald, remaining)));
				
				this.setSuccessful();
			}
		}

		@Override
		public void prepareResponse(NBTTagCompound nbt) {}

		@Override
		public void processResponse(NBTTagCompound nbt) {}
	}
}