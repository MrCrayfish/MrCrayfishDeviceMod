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

/**
 * <p>The Bank is a built in currency system that you can use in your application.
 * You should definitely use this instead of writing your own as this would allow
 * people to use the same currency across applications, not just your own.</p>
 * 
 * <p>Please keep in mind that 1 unit equals 1 emerald. Players can withdraw currency
 * from their account into emeralds. Be nice, and don't abuse this system. Check out
 * the example applications to learn how you use this currency system.</p>
 * 
 * @author MrCrayfish
 */
public class Bank
{
	public static final Bank INSTANCE = new Bank();
	
	private Map<UUID, Account> uuidToAccount = new HashMap<UUID, Account>();
	
	private Bank() {}
	
	/**
	 * Sends a request to get the balance of this user's account. To actually get 
	 * the balance, you need to implement a {@link com.mrcrayfish.device.api.task.Callback} 
	 * and get the integer with the key "balance" from the NBT parameter.
	 * 
	 * @param callback he callback object to processing the response
	 */
	public static void getBalance(Callback callback)
	{
		TaskManager.sendRequest(new TaskGetBalance().setCallback(callback));
	}
	
	/**
	 * <p>TODO. This function is not yet implemented.</p>
	 * 
	 * <p>Sends a request for the user to pay x amount from their account. Use the callback
	 * to check if payment was successful. You will also get returned their new balance. Use
	 * the key "balance" to an integer from the NBT parameter in callback.</p>
	 * 
	 * @param callback he callback object to processing the response
	 */
	public static void pay(Callback callback)
	{
		//TaskManager.sendRequest(new TaskPay().setCallback(callback));
	}
	
	/**
	 * <p>TODO. This function is not yet implemented.</p>
	 * 
	 * <p>Sends a request to add x amount to the user's account. Use the callback
	 * to check if addition was successful. You will also get returned their new balance. Use
	 * the key "balance" to an integer from the NBT parameter in callback.</p>
	 * 
	 * @param callback he callback object to processing the response
	 */
	public static void add(Callback callback)
	{
		//TaskManager.sendRequest(new TaskRemove().setCallback(callback));
	}
	
	//TODO: Make private. Only the bank application should have access to these.
	
	/**
	 * DO NOT USE. NOT AVAILABLE IN FINAL API
	 */
	public static void deposit(int amount)
	{
		deposit(amount, null);
	}
	
	/**
	 * DO NOT USE. NOT AVAILABLE IN FINAL API
	 */
	public static void deposit(int amount, Callback callback) 
	{
		TaskManager.sendRequest(new TaskDeposit(amount).setCallback(callback));
	}
	
	/**
	 * DO NOT USE. NOT AVAILABLE IN FINAL API
	 */
	public static void withdraw(int amount)
	{
		withdraw(amount, null);
	}
	
	/**
	 * DO NOT USE. NOT AVAILABLE IN FINAL API
	 */
	public static void withdraw(int amount, Callback callback) 
	{
		TaskManager.sendRequest(new TaskWithdraw(amount).setCallback(callback));
	}
	
	/**
	 * DO NOT USE. NOT AVAILABLE IN FINAL API
	 */
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
		private int balance;
		
		private Account() {}
		
		public int getBalance()
		{
			return balance;
		}
		
		public boolean hasAmount(int amount)
		{
			return amount <= this.balance;
		}
		
		public boolean deposit(int amount)
		{
			if(amount > 0)
			{
				this.balance += amount;
				return true;
			}
			return false;
		}
		
		public boolean withdraw(int amount)
		{
			if(hasAmount(amount))
			{
				this.balance -= amount;
				return true;
			}
			return false;
		}
	}
	
	public static class TaskGetBalance extends Task 
	{
		private int balance;
		
		public TaskGetBalance()
		{
			super("bank_get_balance");
		}

		@Override
		public void prepareRequest(NBTTagCompound nbt) {}

		@Override
		public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player)
		{
			Account account = Bank.INSTANCE.getAccount(player);
			this.balance = account.getBalance();
			this.setSuccessful();
		}

		@Override
		public void prepareResponse(NBTTagCompound nbt)
		{
			nbt.setInteger("balance", this.balance);
		}

		@Override
		public void processResponse(NBTTagCompound nbt) {}
		
	}
	
	public static class TaskDeposit extends Task 
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
					this.amount = account.getBalance();
					this.setSuccessful();
				}
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
	
	public static class TaskWithdraw extends Task 
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
				if(remaining > 0)
				{
					world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(Items.emerald, remaining)));
				}
				
				this.amount = account.getBalance();
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
}