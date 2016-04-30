package com.mrcrayfish.device.app;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.Image;
import com.mrcrayfish.device.app.components.ItemList;
import com.mrcrayfish.device.app.components.Label;
import com.mrcrayfish.device.app.components.Spinner;
import com.mrcrayfish.device.app.components.TextField;
import com.mrcrayfish.device.app.listener.ClickListener;
import com.mrcrayfish.device.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.app.requests.TaskCheckEmailAccount;
import com.mrcrayfish.device.app.requests.TaskRegisterEmailAccount;
import com.mrcrayfish.device.app.requests.TaskSendEmail;
import com.mrcrayfish.device.app.requests.TaskUpdateInbox;
import com.mrcrayfish.device.task.Callback;
import com.mrcrayfish.device.task.TaskManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

public class ApplicationEmail extends Application
{
	private static final ResourceLocation ENDER_MAIL_ICONS = new ResourceLocation("cdm:textures/gui/ender_mail.png");
	
	private Layout layoutInit;
	private Spinner spinnerInit;
	private Label labelLoading;
	
	private Layout layoutMainMenu;
	private Image logo;
	private Label labelLogo;
	private Button btnRegisterAccount;
	private Button btnInbox;
	
	private Layout layoutRegisterAccount;
	private Label labelEmail;
	private TextField fieldEmail;
	private Label labelDomain;
	private Button btnRegister;
	
	private Layout layoutInbox;
	private ItemList<Email> listEmails;
	private Button btnViewEmail;
	private Button btnNewEmail;
	private Button btnDeleteEmail;
	private Button btnRefresh;
	
	
	public ApplicationEmail() 
	{
		super("email", "Ender Mail", ApplicationBar.APP_BAR_GUI, 28, 46);
	}
	
	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);
		
		this.layoutInit = new Layout(40, 40);
		
		this.spinnerInit = new Spinner(x, y, 14, 10);
		layoutInit.addComponent(this.spinnerInit);
		
		this.labelLoading = new Label("Loading...", x, y, 2, 26);
		layoutInit.addComponent(this.labelLoading);
		
		this.layoutMainMenu = new Layout(100, 75);
		
		this.logo = new Image(x, y, 35, 5, 28, 28, u, v, 14, 14, icon);
		layoutMainMenu.addComponent(this.logo);
		
		this.labelLogo = new Label("Ender Mail", x, y, 19, 35);
		layoutMainMenu.addComponent(labelLogo);
		
		this.btnRegisterAccount = new Button("Register", x, y, 5, 50, 90, 20);
		this.btnRegisterAccount.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				ApplicationEmail.this.setCurrentLayout(layoutRegisterAccount);
			}
		});
		this.btnRegisterAccount.setVisible(false);
		layoutMainMenu.addComponent(this.btnRegisterAccount);
		
		this.btnInbox = new Button("Go to Inbox", x, y, 5, 50, 90, 20);
		this.btnInbox.setClickListener(new ClickListener() {
			
			@Override
			public void onClick(Component c, int mouseButton) {
				TaskManager.sendRequest(new TaskSendEmail(new Email("hi", "hi", "hi"), "fishman"));
			}
		});
		this.btnInbox.setVisible(false);
		layoutMainMenu.addComponent(this.btnInbox);
		
		
		this.layoutRegisterAccount = new Layout(167, 60);
		
		this.labelEmail = new Label("Email", x, y, 5, 5);
		layoutRegisterAccount.addComponent(this.labelEmail);
		
		this.fieldEmail = new TextField(Minecraft.getMinecraft().fontRendererObj, x, y, 5, 15, 80);
		layoutRegisterAccount.addComponent(this.fieldEmail);
		
		this.labelDomain = new Label("@endermail.com", x, y, 88, 18);
		layoutRegisterAccount.addComponent(this.labelDomain);
		
		this.btnRegister = new Button("Register", x, y, 5, 35, 157, 20);
		this.btnRegister.setClickListener(new ClickListener() 
		{
			@Override
			public void onClick(Component c, int mouseButton) 
			{
				int length = fieldEmail.getText().length();
				if(length > 0 && length <= 10)
				{
					TaskRegisterEmailAccount taskRegisterAccount = new TaskRegisterEmailAccount(fieldEmail.getText());
					taskRegisterAccount.setCallback(new Callback() 
					{
						@Override
						public void execute(boolean success) 
						{
							if(success)
							{
								ApplicationEmail.this.setCurrentLayout(layoutInbox);
							}
							else
							{
								fieldEmail.setTextColour(Color.RED);
							}
						}
					});
					TaskManager.sendRequest(taskRegisterAccount);
				}
			}
		});
		layoutRegisterAccount.addComponent(this.btnRegister);
		
		this.layoutInbox = new Layout(300, 148);
		
		this.listEmails = new ItemList<Email>(x, y, 5, 25, 275, 4);
		this.listEmails.setListItemRenderer(new ListItemRenderer<Email>(28) 
		{
			@Override
			public void render(Email e, Gui gui, Minecraft mc, int x, int y, int width, boolean selected) 
			{
				if(selected)
				{
					gui.drawRect(x, y, x + width, y + getHeight(), Color.DARK_GRAY.getRGB());
				}
				else
				{
					gui.drawRect(x, y, x + width, y + getHeight(), Color.GRAY.getRGB());
				}
				mc.fontRendererObj.drawString(e.subject, x + 5, y + 5, Color.WHITE.getRGB());
				mc.fontRendererObj.drawString(e.author + "@endermail.com", x + 5, y + 18, Color.LIGHT_GRAY.getRGB());
			}
		});
		layoutInbox.addComponent(listEmails);
		
		
		this.btnViewEmail = new Button(x, y, 5, 5, ENDER_MAIL_ICONS, 30, 0, 10, 10);
		layoutInbox.addComponent(this.btnViewEmail);
		
		this.btnNewEmail = new Button(x, y, 25, 5, ENDER_MAIL_ICONS, 0, 0, 10, 10);
		this.btnNewEmail.setClickListener(new ClickListener() 
		{
			@Override
			public void onClick(Component c, int mouseButton) 
			{
				TaskManager.sendRequest(new TaskSendEmail(new Email("hi", "hi", "hi"), "b"));
				TaskUpdateInbox taskUpdateInbox = new TaskUpdateInbox("b");
				taskUpdateInbox.setCallback(new Callback() 
				{
					@Override
					public void execute(boolean success) 
					{
						listEmails.removeAll();
						for(Email email : EmailManager.inbox) 
						{
							listEmails.addItem(email);
						}
					}
				});
				TaskManager.sendRequest(taskUpdateInbox);
			}
		});
		layoutInbox.addComponent(this.btnNewEmail);
		
		this.btnDeleteEmail = new Button(x, y, 45, 5, ENDER_MAIL_ICONS, 10, 0, 10, 10);
		layoutInbox.addComponent(this.btnDeleteEmail);
		
		this.btnRefresh = new Button(x, y, 65, 5, ENDER_MAIL_ICONS, 20, 0, 10, 10);
		layoutInbox.addComponent(this.btnRefresh);
		
		this.setCurrentLayout(layoutInit);
		
		TaskCheckEmailAccount taskCheckAccount = new TaskCheckEmailAccount();
		taskCheckAccount.setCallback(new Callback() 
		{
			@Override
			public void execute(boolean success) 
			{
				System.out.println(success);
				if(success)
				{
					ApplicationEmail.this.setCurrentLayout(layoutInbox);
					listEmails.removeAll();
					for(Email email : EmailManager.inbox) {
						listEmails.addItem(email);
						System.out.println("Added mail");
					}
				}
				else
				{
					ApplicationEmail.this.setCurrentLayout(layoutMainMenu);
					btnRegisterAccount.setVisible(true);
				}
			}
		});
		TaskManager.sendRequest(taskCheckAccount);
	}

	@Override
	public void load(NBTTagCompound tagCompound) 
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) 
	{
		
	}
	
	public static class EmailManager
	{
		public static List<Email> inbox = new ArrayList<Email>();
		private static Map<UUID, String> uuidToName = new HashMap<UUID, String>();
		private static Map<String, List<Email>> uuidToInbox = new HashMap<String, List<Email>>();

		public static boolean addEmailToInbox(Email email, String to)
		{
			if(uuidToInbox.containsKey(to))
			{
				return uuidToInbox.get(to).add(email);
			}
			return false;
		}
		
		public static List<Email> getEmailsForAccount(String name)
		{
			for(String key : uuidToInbox.keySet())
			{
				System.out.println(key);
			}
			return uuidToInbox.get(name);
		}
		
		public static boolean addAccount(EntityPlayer player, String name)
		{
			System.out.println("Registering?");
			System.out.println(uuidToName.get(player.getUniqueID()));
			if(!uuidToName.containsKey(player.getUniqueID()))
			{
				System.out.println("Blah");
				if(!uuidToName.containsValue(name))
				{
					System.out.println("Registering account '" + name + "'");
					uuidToName.put(player.getUniqueID(), name);
					uuidToInbox.put(name, new ArrayList<Email>());
					return true;
				}
			}
			return false;
		}
		
		public static boolean hasAccount(UUID uuid)
		{
			System.out.println(uuid);
			for(UUID id : uuidToName.keySet())
			{
				System.out.println(id);
			}
			return uuidToName.containsKey(uuid);
		}

		public static void readFromNBT(NBTTagCompound nbt) 
		{
			uuidToInbox.clear();
			
			NBTTagList inboxes = (NBTTagList) nbt.getTag("Inboxes");
			for(int i = 0; i < inboxes.tagCount(); i++)
			{
				NBTTagCompound inbox = inboxes.getCompoundTagAt(i);
				String name = inbox.getString("Name");
				
				List<Email> emails = new ArrayList<Email>();
				NBTTagList emailTagList = (NBTTagList) inbox.getTag("Emails");
				for(int j = 0; j < emailTagList.tagCount(); j++)
				{
					NBTTagCompound emailTag = emailTagList.getCompoundTagAt(j);
					Email email = Email.readFromNBT(emailTag);
					emails.add(email);
				}
				uuidToInbox.put(name, emails);
			}
			
			uuidToName.clear();
			
			NBTTagList accounts = (NBTTagList) nbt.getTag("Accounts");
			for(int i = 0; i < accounts.tagCount(); i++)	
			{
				NBTTagCompound account = accounts.getCompoundTagAt(i);
				UUID uuid = UUID.fromString(account.getString("UUID"));
				String name = account.getString("Name");
				uuidToName.put(uuid, name);
			}
		}

		public static void writeToNBT(NBTTagCompound nbt) 
		{
			NBTTagList inboxes = new NBTTagList();
			for(String key : uuidToInbox.keySet())
			{
				NBTTagCompound inbox = new NBTTagCompound();
				inbox.setString("Name", key);
				
				NBTTagList emailTagList = new NBTTagList();
				List<Email> emails = uuidToInbox.get(key);
				for(Email email : emails)
				{
					NBTTagCompound emailTag = new NBTTagCompound();
					email.writeToNBT(emailTag);
					emailTagList.appendTag(emailTag);
				}
				inbox.setTag("Emails", emailTagList);
				inboxes.appendTag(inbox);
			}
			nbt.setTag("Inboxes", inboxes);
			
			NBTTagList accounts = new NBTTagList();
			for(UUID key : uuidToName.keySet())
			{
				NBTTagCompound account = new NBTTagCompound();
				account.setString("UUID", key.toString());
				account.setString("Name", uuidToName.get(key));
				accounts.appendTag(account);
			}
			nbt.setTag("Accounts", accounts);
		}
		
		public static void clear()
		{
			uuidToInbox.clear();
			uuidToName.clear();
			inbox.clear();
		}
	}
	
	public static class Email
	{
		private String subject, author, message;

		public Email(String subject, String author, String message) 
		{
			this.subject = subject;
			this.author = author;
			this.message = message;
		}

		public String getSubject() 
		{
			return subject;
		}

		public String getAuthor() 
		{
			return author;
		}

		public String getMessage() 
		{
			return message;
		}
		
		public void writeToNBT(NBTTagCompound nbt)
		{
			nbt.setString("subject", this.subject);
			nbt.setString("author", this.author);
			nbt.setString("message", this.message);
		}
		
		public static Email readFromNBT(NBTTagCompound nbt)
		{
			return new Email(nbt.getString("subject"), nbt.getString("author"), nbt.getString("message"));
		}
	}
}
