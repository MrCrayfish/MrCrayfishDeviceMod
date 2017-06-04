package com.mrcrayfish.device.programs.system;

import java.awt.Color;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskProxy;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.TaskBar;
import com.mrcrayfish.device.programs.system.object.Account;
import com.mrcrayfish.device.util.InventoryUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ApplicationBank extends Application
{
	private static boolean registered = false;
	
	private static final ItemStack EMERALD = new ItemStack(Items.EMERALD);
	private static final ResourceLocation BANK_ASSETS = new ResourceLocation("cdm:textures/gui/bank.png");
	private static final ResourceLocation villagerTextures = new ResourceLocation("textures/entity/villager/villager.png");
    private static final ModelVillager villagerModel = new ModelVillager(0.0F);
	
	private Layout layoutStart;
	private Label labelTeller;
	private Text textWelcome;
	private Button btnDepositWithdraw;
	private Button btnTransfer;
	
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
	private int rotation;
	
	public ApplicationBank()
	{
		super(Reference.MOD_ID + "Bank", "The Emerald Bank");
		this.setIcon(TaskBar.APP_BAR_GUI, 98, 30);
		this.registerTasks();
	}
	
	@Override
	public void onTick()
	{
		super.onTick();
		rotation++;
		if(rotation >= 100) {
			rotation = 0;
		}
	}
	
	@Override
	public void init()
	{
		layoutStart = new Layout();
		layoutStart.setBackground(new Background()
		{
			private float scaleX = 0F;
			private float scaleY = 0;
			
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
			{
				GlStateManager.pushMatrix();
				{
					GlStateManager.translate(x + 25, y + 33, 15);
					GlStateManager.scale((float) -2.5, (float) -2.5, (float) -2.5);
					GlStateManager.rotate(-10F, 1, 0, 0);
					GlStateManager.rotate(180F, 0, 0, 1);
					GlStateManager.rotate(-20F, 0, 1, 0);
					scaleX = (mouseX - x - 25) / (float) width;
					scaleY = (mouseY - y - 20) / (float) height;
					mc.getTextureManager().bindTexture(villagerTextures);
					villagerModel.render(null, 0F, 0F, 0F, -70F * scaleX + 20F, 30F * scaleY, 1F);
					GlStateManager.disableDepth();
				}
				GlStateManager.popMatrix();
				
				mc.getTextureManager().bindTexture(BANK_ASSETS);
				RenderUtil.drawRectWithTexture(x + 46, y + 19, 0, 0, 146, 52, 146, 52);
			}
		});
		
		labelTeller = new Label(TextFormatting.YELLOW + "Casey The Teller", 60, 7);
		layoutStart.addComponent(labelTeller);
		
		textWelcome = new Text(TextFormatting.BLACK + "Hello " + Minecraft.getMinecraft().player.getName() + ", welcome to The Emerald Bank! How can I help you?", 62, 25, 125);
		layoutStart.addComponent(textWelcome);
		
		btnDepositWithdraw = new Button("View Account", 54, 74, 76, 20);
		btnDepositWithdraw.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				
			}
		});
		btnDepositWithdraw.setToolTip("View Account", "Shows your balance");
		layoutStart.addComponent(btnDepositWithdraw);
		
		btnTransfer = new Button("Transfer", 133, 74, 58, 20);
		btnTransfer.setToolTip("Transfer", "Withdraw and deposit emeralds");
		btnTransfer.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				setCurrentLayout(layoutMain);
			}
		});
		layoutStart.addComponent(btnTransfer);
		
		setCurrentLayout(layoutStart);
		
		layoutMain = new Layout(120, 143);
		layoutMain.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
			{
				gui.drawRect(x, y, x + width, y + 40, Color.GRAY.getRGB());
				gui.drawRect(x, y + 39, x + width, y + 40, Color.DARK_GRAY.getRGB());
				gui.drawRect(x + 62, y + 103, x + 115, y + 138, Color.BLACK.getRGB());
				gui.drawRect(x + 63, y + 104, x + 114, y + 113, Color.DARK_GRAY.getRGB());
				gui.drawRect(x + 63, y + 114, x + 114, y + 137, Color.GRAY.getRGB());
				RenderUtil.renderItem(x + 65, y + 118, EMERALD, false);
			}
		});
		
		labelBalance = new Label("Balance", 60, 5);
		labelBalance.setAlignment(Label.ALIGN_CENTER);
		labelBalance.setShadow(false);
		layoutMain.addComponent(labelBalance);
		
		labelAmount = new Label("Loading balance...", 60, 18);
		labelAmount.setAlignment(Label.ALIGN_CENTER);
		labelAmount.setScale(2);
		layoutMain.addComponent(labelAmount);
		
		amountField = new TextField(5, 45, 110);
		amountField.setText("0");
		amountField.setEditable(false);
		layoutMain.addComponent(amountField);
		
		btnOne = new Button("1", 5, 103, 16, 16);
		addNumberClickListener(btnOne, amountField, 1);
		layoutMain.addComponent(btnOne);
		
		btnTwo = new Button("2", 24, 103, 16, 16);
		addNumberClickListener(btnTwo, amountField, 2);
		layoutMain.addComponent(btnTwo);
		
		btnThree = new Button("3", 43, 103, 16, 16);
		addNumberClickListener(btnThree, amountField, 3);
		layoutMain.addComponent(btnThree);
		
		btnFour = new Button("4", 5, 84, 16, 16);
		addNumberClickListener(btnFour, amountField, 4);
		layoutMain.addComponent(btnFour);
		
		btnFive = new Button("5", 24, 84, 16, 16);
		addNumberClickListener(btnFive, amountField, 5);
		layoutMain.addComponent(btnFive);
		
		btnSix = new Button("6", 43, 84, 16, 16);
		addNumberClickListener(btnSix, amountField, 6);
		layoutMain.addComponent(btnSix);
		
		btnSeven = new Button("7", 5, 65, 16, 16);
		addNumberClickListener(btnSeven, amountField, 7);
		layoutMain.addComponent(btnSeven);
		
		btnEight = new Button("8", 24, 65, 16, 16);
		addNumberClickListener(btnEight, amountField, 8);
		layoutMain.addComponent(btnEight);
		
		btnNine = new Button("9", 43, 65, 16, 16);
		addNumberClickListener(btnNine, amountField, 9);
		layoutMain.addComponent(btnNine);
		
		btnZero = new Button("0", 5, 122, 16, 16);
		addNumberClickListener(btnZero, amountField, 0);
		layoutMain.addComponent(btnZero);
		
		btnClear = new Button("Clr", 24, 122, 35, 16);
		btnClear.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				amountField.setText("0");
			}
		});
		layoutMain.addComponent(btnClear);
		
		buttonDeposit = new Button("Deposit", 62, 65, 53, 16);
		buttonDeposit.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(amountField.getText().equals("0")) {
					return;
				}
				
				final int amount = Integer.parseInt(amountField.getText());
				deposit(amount, new Callback()
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
		
		buttonWithdraw = new Button("Withdraw", 62, 84, 53, 16);
		buttonWithdraw.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(amountField.getText().equals("0")) {
					return;
				}
				
				final int amount = Integer.parseInt(amountField.getText());
				withdraw(Integer.parseInt(amountField.getText()), new Callback()
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
		
		emeraldAmount = InventoryUtil.getItemAmount(Minecraft.getMinecraft().player, Items.EMERALD);
		labelEmeraldAmount = new Label("x " + emeraldAmount, 83, 123);
		layoutMain.addComponent(labelEmeraldAmount);
		
		labelInventory = new Label("Wallet", 74, 105);
		labelInventory.setShadow(false);
		layoutMain.addComponent(labelInventory);

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

	private void deposit(int amount, Callback callback) 
	{
		TaskProxy.sendTask(new TaskDeposit(amount).setCallback(callback));
	}
	
	private void withdraw(int amount, Callback callback) 
	{
		TaskProxy.sendTask(new TaskWithdraw(amount).setCallback(callback));
	}
	
	public void registerTasks()
	{
		if(!registered)
		{
			TaskProxy.registerTask(TaskDeposit.class);
			TaskProxy.registerTask(TaskWithdraw.class);
			registered = true;
		}
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{
		
	}
	
	public static class TaskDeposit extends Task 
	{
		private int amount;
		
		private TaskDeposit()
		{
			super("bank_deposit");
		}
		
		private TaskDeposit(int amount)
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
			if(InventoryUtil.removeItemWithAmount(player, Items.EMERALD, amount))
			{
				Account account = BankUtil.INSTANCE.getAccount(player);
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
		
		private TaskWithdraw()
		{
			super("bank_withdraw");
		}
		
		private TaskWithdraw(int amount)
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
			Account account = BankUtil.INSTANCE.getAccount(player);
			if(account.withdraw(amount))
			{
				int stacks = amount / 64;
				for(int i = 0; i < stacks; i++)
				{
					world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(Items.EMERALD, 64)));
				}
				
				int remaining = amount % 64;
				if(remaining > 0)
				{
					world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(Items.EMERALD, remaining)));
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
