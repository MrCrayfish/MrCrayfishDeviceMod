package com.mrcrayfish.device.programs.system.object;

import java.util.UUID;

import com.mrcrayfish.device.api.utils.BankUtil;

public class Account 
{
	private int balance;
	
	public Account(int balance) 
	{
		this.balance = balance;
	}
	
	public int getBalance()
	{
		return balance;
	}
	
	public boolean hasAmount(int amount)
	{
		return amount <= this.balance;
	}
	
	public void add(int amount) 
	{
		if(amount > 0) {
			this.balance += amount;
		}
	}
	
	public void remove(int amount)
	{
		this.balance -= amount;
		if(this.balance < 0) {
			this.balance = 0;
		}
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
	
	public boolean pay(Account reciever, int amount)
	{
		if(reciever != null)
		{
			if(hasAmount(amount))
			{
				this.balance -= amount;
				reciever.balance += amount;
				return true;
			}
		}
		return false;
	}
}
