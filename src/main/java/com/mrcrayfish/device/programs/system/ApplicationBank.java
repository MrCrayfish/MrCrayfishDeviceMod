package com.mrcrayfish.device.programs.system;

import java.awt.Color;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.utils.Bank;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationBank extends Application
{
	private Layout layoutMain;
	private Label labelBalance;
	private Label labelAmount;
	private TextField amountField;
	private Button btnOne;
	private Button btnTwo;
	private Button btnThree;
	private Button btnFour;
	private Button btnFive;
	private Button btnSix;
	private Button btnSeven;
	private Button btnEight;
	private Button btnNine;
	private Button btnZero;
	private Button btnClear;
	
	public ApplicationBank()
	{
		super(Reference.MOD_ID + "Bank", "The Emerald Bank");
	}
	
	@Override
	public void init(int x, int y)
	{
		layoutMain = new Layout(120, 143);
		layoutMain.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				gui.drawRect(x, y, x + width, y + 40, Color.GRAY.getRGB());
				gui.drawRect(x, y + 39, x + width, y + 40, Color.DARK_GRAY.getRGB());
			}
		});
		
		labelBalance = new Label("Balance", x, y, 60, 5);
		labelBalance.setAlignment(Label.ALIGN_CENTER);
		labelBalance.setShadow(false);
		layoutMain.addComponent(labelBalance);
		
		labelAmount = new Label("Loading balance...", x, y, 60, 18);
		labelAmount.setAlignment(Label.ALIGN_CENTER);
		labelAmount.setScale(2);
		layoutMain.addComponent(labelAmount);
		
		amountField = new TextField(x, y, 5, 45, 110);
		layoutMain.addComponent(amountField);
		
		btnOne = new Button("1", x, y, 5, 65, 16, 16);
		
		btnTwo = new Button("2", x, y, 5, 84, 16, 16);
		
		btnOne = new Button("1", x, y, 5, 103, 16, 16);
		layoutMain.addComponent(btnOne);
		
		btnTwo = new Button("2", x, y, 24, 103, 16, 16);
		layoutMain.addComponent(btnTwo);
		
		btnThree = new Button("3", x, y, 43, 103, 16, 16);
		layoutMain.addComponent(btnThree);
		
		btnFour = new Button("4", x, y, 5, 84, 16, 16);
		layoutMain.addComponent(btnFour);
		
		btnFive = new Button("5", x, y, 24, 84, 16, 16);
		layoutMain.addComponent(btnFive);
		
		btnSix = new Button("6", x, y, 43, 84, 16, 16);
		layoutMain.addComponent(btnSix);
		
		btnSeven = new Button("7", x, y, 5, 65, 16, 16);
		layoutMain.addComponent(btnSeven);
		
		btnEight = new Button("8", x, y, 24, 65, 16, 16);
		layoutMain.addComponent(btnEight);
		
		btnNine = new Button("9", x, y, 43, 65, 16, 16);
		layoutMain.addComponent(btnNine);
		
		btnZero = new Button("0", x, y, 5, 122, 16, 16);
		layoutMain.addComponent(btnZero);
		
		btnClear = new Button("Clr", x, y, 24, 122, 35, 16);
		layoutMain.addComponent(btnClear);
		
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
							labelAmount.setText("$" + balance);
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
							labelAmount.setText("$" + balance);
							amountField.clear();
						}
					}
				});
			}
		});
		this.addComponent(buttonWithdraw);
		
		setCurrentLayout(layoutMain);
		
		Bank.getBalance(new Callback()
		{
			@Override
			public void execute(NBTTagCompound nbt, boolean success)
			{
				if(success)
				{
					int balance = nbt.getInteger("balance");
					labelAmount.setText("$" + balance);
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
