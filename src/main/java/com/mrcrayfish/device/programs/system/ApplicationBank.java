package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.utils.Bank;
import com.mrcrayfish.device.api.utils.OnlineRequest;
import com.mrcrayfish.device.api.utils.OnlineRequest.ResponseHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationBank extends Application
{
	public ApplicationBank()
	{
		super(Reference.MOD_ID + "Bank", "The Emerald Bank");
	}
	
	@Override
	public void init(int x, int y)
	{
		super.init(x, y);
		
		final Label label = new Label("Loading balance...", x, y, 5, 5);
		this.addComponent(label);
		
		final TextField amountField = new TextField(x, y, 5, 15, 70);
		this.addComponent(amountField);
		
		final Button buttonDeposit = new Button("Deposit", x, y, 5, 35, 30, 16);
		buttonDeposit.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				Bank.INSTANCE.deposit(Integer.parseInt(amountField.getText()), new Callback()
				{
					@Override
					public void execute(NBTTagCompound nbt, boolean success)
					{
						if(success)
						{
							int balance = nbt.getInteger("balance");
							label.setText("$" + balance);
							amountField.clear();
						}
					}
				});
			}
		});
		this.addComponent(buttonDeposit);
		
		final Button buttonWithdraw = new Button("Withdraw", x, y, 40, 35, 30, 16);
		buttonWithdraw.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				Bank.INSTANCE.withdraw(Integer.parseInt(amountField.getText()), new Callback()
				{
					@Override
					public void execute(NBTTagCompound nbt, boolean success)
					{
						if(success)
						{
							int balance = nbt.getInteger("balance");
							label.setText("$" + balance);
							amountField.clear();
						}
					}
				});
			}
		});
		this.addComponent(buttonWithdraw);
		
		Bank.getBalance(new Callback()
		{
			@Override
			public void execute(NBTTagCompound nbt, boolean success)
			{
				if(success)
				{
					int balance = nbt.getInteger("balance");
					label.setText("$" + balance);
				}
			}
		});
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{
		
	}

}
