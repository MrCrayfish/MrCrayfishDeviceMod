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
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.api.utils.InventoryUtil;
import com.mrcrayfish.device.api.utils.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationBank extends Application
{
	private static final ItemStack EMERALD = new ItemStack(Items.emerald);
	
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
	private Button buttonDeposit;
	private Button buttonWithdraw;
	private Label labelEmeraldAmount;
	private Label labelInventory;
	
	private int emeraldAmount;
	
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
				gui.drawRect(x + 62, y + 103, x + 115, y + 138, Color.BLACK.getRGB());
				gui.drawRect(x + 63, y + 104, x + 114, y + 113, Color.DARK_GRAY.getRGB());
				gui.drawRect(x + 63, y + 114, x + 114, y + 137, Color.GRAY.getRGB());
				RenderUtil.renderItem(x + 65, y + 118, EMERALD);
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
		amountField.setText("0");
		amountField.setEditable(false);
		layoutMain.addComponent(amountField);
		
		btnOne = new Button("1", x, y, 5, 103, 16, 16);
		addNumberClickListener(btnOne, amountField, 1);
		layoutMain.addComponent(btnOne);
		
		btnTwo = new Button("2", x, y, 24, 103, 16, 16);
		addNumberClickListener(btnTwo, amountField, 2);
		layoutMain.addComponent(btnTwo);
		
		btnThree = new Button("3", x, y, 43, 103, 16, 16);
		addNumberClickListener(btnThree, amountField, 3);
		layoutMain.addComponent(btnThree);
		
		btnFour = new Button("4", x, y, 5, 84, 16, 16);
		addNumberClickListener(btnFour, amountField, 4);
		layoutMain.addComponent(btnFour);
		
		btnFive = new Button("5", x, y, 24, 84, 16, 16);
		addNumberClickListener(btnFive, amountField, 5);
		layoutMain.addComponent(btnFive);
		
		btnSix = new Button("6", x, y, 43, 84, 16, 16);
		addNumberClickListener(btnSix, amountField, 6);
		layoutMain.addComponent(btnSix);
		
		btnSeven = new Button("7", x, y, 5, 65, 16, 16);
		addNumberClickListener(btnSeven, amountField, 7);
		layoutMain.addComponent(btnSeven);
		
		btnEight = new Button("8", x, y, 24, 65, 16, 16);
		addNumberClickListener(btnEight, amountField, 8);
		layoutMain.addComponent(btnEight);
		
		btnNine = new Button("9", x, y, 43, 65, 16, 16);
		addNumberClickListener(btnNine, amountField, 9);
		layoutMain.addComponent(btnNine);
		
		btnZero = new Button("0", x, y, 5, 122, 16, 16);
		addNumberClickListener(btnZero, amountField, 0);
		layoutMain.addComponent(btnZero);
		
		btnClear = new Button("Clr", x, y, 24, 122, 35, 16);
		btnClear.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				amountField.setText("0");
			}
		});
		layoutMain.addComponent(btnClear);
		
		buttonDeposit = new Button("Deposit", x, y, 62, 65, 53, 16);
		buttonDeposit.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(amountField.getText().equals("0")) {
					return;
				}
				
				final int amount = Integer.parseInt(amountField.getText());
				BankUtil.INSTANCE.deposit(amount, new Callback()
				{
					@Override
					public void execute(NBTTagCompound nbt, boolean success)
					{
						if(success)
						{
							updateEmeraldCount(-amount);
							int balance = nbt.getInteger("balance");
							labelAmount.setText("$" + balance);
							amountField.setText("0");
						}
					}
				});
			}
		});
		layoutMain.addComponent(buttonDeposit);
		
		buttonWithdraw = new Button("Withdraw", x, y, 62, 84, 53, 16);
		buttonWithdraw.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(amountField.getText().equals("0")) {
					return;
				}
				
				final int amount = Integer.parseInt(amountField.getText());
				BankUtil.INSTANCE.withdraw(Integer.parseInt(amountField.getText()), new Callback()
				{
					@Override
					public void execute(NBTTagCompound nbt, boolean success)
					{
						if(success)
						{
							updateEmeraldCount(amount);
							int balance = nbt.getInteger("balance");
							labelAmount.setText("$" + balance);
							amountField.setText("0");
						}
					}
				});
			}
		});
		layoutMain.addComponent(buttonWithdraw);
		
		emeraldAmount = InventoryUtil.getItemAmount(Minecraft.getMinecraft().thePlayer, Items.emerald);
		labelEmeraldAmount = new Label("x " + emeraldAmount, x, y, 83, 123);
		layoutMain.addComponent(labelEmeraldAmount);
		
		labelInventory = new Label("Wallet", x, y, 74, 105);
		labelInventory.setShadow(false);
		layoutMain.addComponent(labelInventory);
		
		setCurrentLayout(layoutMain);
		
		BankUtil.getBalance(new Callback()
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
	
	public void addNumberClickListener(Button btn, final TextField field, final int number) 
	{
		btn.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(!(field.getText().equals("0") && number == 0)) {
					if(field.getText().equals("0")) field.clear();
					field.writeText(Integer.toString(number));
				}
			}
		});
	}
	
	public void updateEmeraldCount(int amount) 
	{
		emeraldAmount += amount;
		labelEmeraldAmount.setText("x " + emeraldAmount);
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
