package com.mrcrayfish.device.programs.email;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Spinner;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.InitListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.TaskProxy;
import com.mrcrayfish.device.core.TaskBar;
import com.mrcrayfish.device.network.task.TaskManager;
import com.mrcrayfish.device.programs.email.task.TaskCheckEmailAccount;
import com.mrcrayfish.device.programs.email.task.TaskDeleteEmail;
import com.mrcrayfish.device.programs.email.task.TaskRegisterEmailAccount;
import com.mrcrayfish.device.programs.email.task.TaskSendEmail;
import com.mrcrayfish.device.programs.email.task.TaskUpdateInbox;
import com.mrcrayfish.device.programs.email.task.TaskViewEmail;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ApplicationEmail extends Application
{
	private static final ResourceLocation ENDER_MAIL_ICONS = new ResourceLocation("cdm:textures/gui/ender_mail.png");

	private static final Pattern EMAIL = Pattern.compile("^([a-zA-Z0-9]{1,10})@endermail\\.com$");
	private final Color COLOR_EMAIL_CONTENT_BACKGROUND = new Color(160, 160, 160);

	/* Loading Layout */
	private Layout layoutInit;
	private Spinner spinnerInit;
	private Label labelLoading;

	/* Main Menu Layout */
	private Layout layoutMainMenu;
	private Image logo;
	private Label labelLogo;
	private Button btnRegisterAccount;

	/* Register Account Layout */
	private Layout layoutRegisterAccount;
	private Label labelEmail;
	private TextField fieldEmail;
	private Label labelDomain;
	private Button btnRegister;

	/* Inbox Layout */
	private Layout layoutInbox;
	private ItemList<Email> listEmails;
	private Button btnViewEmail;
	private Button btnNewEmail;
	private Button btnReplyEmail;
	private Button btnDeleteEmail;
	private Button btnRefresh;

	/* New Email Layout */
	private Layout layoutNewEmail;
	private Label labelTo;
	private TextField fieldRecipient;
	private Label labelSubject;
	private TextField fieldSubject;
	private Label labelMessage;
	private TextArea textAreaMessage;
	private Button btnSendEmail;
	private Button btnCancelEmail;

	/* View Email Layout */
	private Layout layoutViewEmail;
	private Label labelViewSubject;
	private Label labelSender;
	private Label labelFrom;
	private Label labelViewSubjectContent;
	private Label labelViewMessage;
	private Text textMessage;
	private Button btnCancelViewEmail;
	
	/* Contacts Layout */
	private Layout layoutContacts;
	private ItemList listContacts;
	private Button btnAddContact;
	private Button btnDeleteContact;
	private Button btnCancelContact;
	
	/* Add Contact Layout */
	private Layout layoutAddContact;
	private Label labelContactNickname;
	private TextField fieldContactNickname;
	private Label labelContactEmail;
	private TextField fieldContactEmail;
	private Button btnSaveContact;
	private Button btnCancelAddContact;
	
	/* Insert Contact Layout */
	private Layout layoutInsertContact;
	private ItemList listContacts2;
	private Button btnInsertContact;
	private Button btnCancelInsertContact;

	private String currentName;
	
	private List<Contact> contacts;

	public ApplicationEmail()
	{
		super("email", "Ender Mail", TaskBar.APP_BAR_GUI, 70, 30);
	}

	@Override
	public void init()
	{
		super.init();

		/* Loading Layout */
		
		layoutInit = new Layout(40, 40);

		spinnerInit = new Spinner(14, 10);
		layoutInit.addComponent(spinnerInit);

		labelLoading = new Label("Loading...", 2, 26);
		layoutInit.addComponent(labelLoading);

		
		/* Main Menu Layout */
		
		layoutMainMenu = new Layout(100, 75);

		logo = new Image(35, 5, 28, 28, u, v, 14, 14, icon);
		layoutMainMenu.addComponent(logo);

		labelLogo = new Label("Ender Mail", 19, 35);
		layoutMainMenu.addComponent(labelLogo);

		btnRegisterAccount = new Button("Register", 5, 50, 90, 20);
		btnRegisterAccount.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutRegisterAccount);
			}
		});
		btnRegisterAccount.setVisible(false);
		layoutMainMenu.addComponent(btnRegisterAccount);

		
		/* Register Account Layout */
		
		layoutRegisterAccount = new Layout(167, 60);

		labelEmail = new Label("Email", 5, 5);
		layoutRegisterAccount.addComponent(labelEmail);

		fieldEmail = new TextField(5, 15, 80);
		layoutRegisterAccount.addComponent(fieldEmail);

		labelDomain = new Label("@endermail.com", 88, 18);
		layoutRegisterAccount.addComponent(labelDomain);

		btnRegister = new Button("Register", 5, 35, 157, 20);
		btnRegister.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				int length = fieldEmail.getText().length();
				if (length > 0 && length <= 10)
				{
					TaskRegisterEmailAccount taskRegisterAccount = new TaskRegisterEmailAccount(fieldEmail.getText());
					taskRegisterAccount.setCallback(new Callback()
					{
						@Override
						public void execute(NBTTagCompound nbt, boolean success)
						{
							if (success)
							{
								currentName = fieldEmail.getText();
								setCurrentLayout(layoutInbox);
							}
							else
							{
								fieldEmail.setTextColour(Color.RED);
							}
						}
					});
					TaskProxy.sendTask(taskRegisterAccount);
				}
			}
		});
		layoutRegisterAccount.addComponent(btnRegister);

		
		/* Inbox Layout */
		
		layoutInbox = new Layout(300, 148);
		layoutInbox.setInitListener(new InitListener()
		{
			@Override
			public void onInit()
			{
				TaskUpdateInbox taskUpdateInbox = new TaskUpdateInbox();
				taskUpdateInbox.setCallback(new Callback()
				{
					@Override
					public void execute(NBTTagCompound nbt, boolean success)
					{
						listEmails.removeAll();
						for (Email email : EmailManager.INSTANCE.inbox)
						{
							listEmails.addItem(email);
						}
					}
				});
				TaskProxy.sendTask(taskUpdateInbox);
			}
		});

		listEmails = new ItemList<Email>(5, 25, 275, 4);
		listEmails.setListItemRenderer(new ListItemRenderer<Email>(28)
		{
			@Override
			public void render(Email e, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				if (selected) gui.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.getRGB());
				else gui.drawRect(x, y, x + width, y + height, Color.GRAY.getRGB());

				if (!e.isRead())
				{
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					mc.getTextureManager().bindTexture(ENDER_MAIL_ICONS);
					gui.drawTexturedModalRect(x + 247, y + 8, 0, 10, 20, 12);
				}

				mc.fontRendererObj.drawString(e.subject, x + 5, y + 5, Color.WHITE.getRGB());
				mc.fontRendererObj.drawString(e.author + "@endermail.com", x + 5, y + 18, Color.LIGHT_GRAY.getRGB());
			}
		});
		layoutInbox.addComponent(listEmails);

		btnViewEmail = new Button(5, 5, ENDER_MAIL_ICONS, 30, 0, 10, 10);
		btnViewEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				int index = listEmails.getSelectedIndex();
				if (index != -1)
				{
					TaskProxy.sendTask(new TaskViewEmail(index));
					Email email = listEmails.getSelectedItem();
					email.setRead(true);
					textMessage.setText(email.message);
					labelViewSubject.setText(email.subject);
					labelFrom.setText(email.author + "@endermail.com");
					setCurrentLayout(layoutViewEmail);
				}
			}
		});
		btnViewEmail.setToolTip("View", "Opens the currently selected email");
		layoutInbox.addComponent(btnViewEmail);

		btnNewEmail = new Button(25, 5, ENDER_MAIL_ICONS, 0, 0, 10, 10);
		btnNewEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutNewEmail);
			}
		});
		btnNewEmail.setToolTip("New Email", "Send an email to a player");
		layoutInbox.addComponent(btnNewEmail);

		btnReplyEmail = new Button(45, 5, ENDER_MAIL_ICONS, 60, 0, 10, 10);
		btnReplyEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				Email email = listEmails.getSelectedItem();
				if (email != null)
				{
					setCurrentLayout(layoutNewEmail);
					fieldRecipient.setText(email.author + "@endermail.com");
					fieldSubject.setText("RE: " + email.subject);
				}
			}
		});
		btnReplyEmail.setToolTip("Reply", "Reply to the currently selected email");
		layoutInbox.addComponent(btnReplyEmail);

		btnDeleteEmail = new Button(65, 5, ENDER_MAIL_ICONS, 10, 0, 10, 10);
		btnDeleteEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				final int index = listEmails.getSelectedIndex();
				if (index != -1)
				{
					TaskDeleteEmail taskDeleteEmail = new TaskDeleteEmail(index);
					taskDeleteEmail.setCallback(new Callback()
					{
						@Override
						public void execute(NBTTagCompound nbt, boolean success)
						{
							listEmails.removeItem(index);
							EmailManager.INSTANCE.getInbox().remove(index);
						}
					});
					TaskProxy.sendTask(taskDeleteEmail);
				}
			}
		});
		btnDeleteEmail.setToolTip("Trash Email", "Deletes the currently select email");
		layoutInbox.addComponent(btnDeleteEmail);

		btnRefresh = new Button(85, 5, ENDER_MAIL_ICONS, 20, 0, 10, 10);
		btnRefresh.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				TaskUpdateInbox taskUpdateInbox = new TaskUpdateInbox();
				taskUpdateInbox.setCallback(new Callback()
				{
					@Override
					public void execute(NBTTagCompound nbt, boolean success)
					{
						listEmails.removeAll();
						for (Email email : EmailManager.INSTANCE.inbox)
						{
							listEmails.addItem(email);
						}
					}
				});
				TaskProxy.sendTask(taskUpdateInbox);
			}
		});
		btnRefresh.setToolTip("Refresh Inbox", "Checks for any new emails");
		layoutInbox.addComponent(btnRefresh);

		
		/* New Email Layout */
		
		layoutNewEmail = new Layout(255, 148);

		labelTo = new Label("To", 5, 8);
		layoutNewEmail.addComponent(labelTo);

		fieldRecipient = new TextField(50, 5, 200);
		layoutNewEmail.addComponent(fieldRecipient);

		labelSubject = new Label("Subject", 5, 26);
		layoutNewEmail.addComponent(labelSubject);

		fieldSubject = new TextField(50, 23, 200);
		layoutNewEmail.addComponent(fieldSubject);

		labelMessage = new Label("Message", 5, 44);
		layoutNewEmail.addComponent(labelMessage);

		textAreaMessage = new TextArea(50, 41, 200, 100);
		layoutNewEmail.addComponent(textAreaMessage);

		btnSendEmail = new Button(6, 60, ENDER_MAIL_ICONS, 50, 0, 10, 10);
		btnSendEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				Matcher matcher = EMAIL.matcher(fieldRecipient.getText());
				if (!matcher.matches()) return;

				Email email = new Email(fieldSubject.getText(), textAreaMessage.getText());
				TaskSendEmail taskSendEmail = new TaskSendEmail(email, matcher.group(1));
				taskSendEmail.setCallback(new Callback()
				{
					@Override
					public void execute(NBTTagCompound nbt, boolean success)
					{
						if (success)
						{
							setCurrentLayout(layoutInbox);
							textAreaMessage.clear();
							fieldSubject.clear();
							fieldRecipient.clear();
						}
						else
						{

						}
					}
				});
				TaskProxy.sendTask(taskSendEmail);
			}
		});
		btnSendEmail.setToolTip("Send", "Send email to recipient");
		layoutNewEmail.addComponent(btnSendEmail);

		btnCancelEmail = new Button(28, 60, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		btnCancelEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutInbox);
				textAreaMessage.clear();
				fieldSubject.clear();
				fieldRecipient.clear();
			}
		});
		btnCancelEmail.setToolTip("Cancel", "Go back to Inbox");
		layoutNewEmail.addComponent(btnCancelEmail);
		
		
		/* View Email Layout */
		
		layoutViewEmail = new Layout(240, 156);
		layoutViewEmail.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				gui.drawRect(x, y + 22, x + layoutViewEmail.width, y + 50, Color.GRAY.getRGB());
				gui.drawRect(x, y + 22, x + layoutViewEmail.width, y + 23, Color.DARK_GRAY.getRGB());
				gui.drawRect(x, y + 49, x + layoutViewEmail.width, y + 50, Color.DARK_GRAY.getRGB());
				gui.drawRect(x, y + 50, x + layoutViewEmail.width, y + 156, COLOR_EMAIL_CONTENT_BACKGROUND.getRGB());
			}
		});

		labelViewSubject = new Label("Subject", 5, 26);
		labelViewSubject.setTextColour(new Color(255, 170, 0));
		layoutViewEmail.addComponent(labelViewSubject);

		labelFrom = new Label("From", 5, 38);
		layoutViewEmail.addComponent(labelFrom);

		btnCancelViewEmail = new Button(5, 3, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		btnCancelViewEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutInbox);
			}
		});
		btnCancelViewEmail.setToolTip("Cancel", "Go back to Inbox");
		layoutViewEmail.addComponent(btnCancelViewEmail);

		textMessage = new Text("Hallo", 5, 54, 230);
		textMessage.setShadow(false);
		layoutViewEmail.addComponent(textMessage);

		setCurrentLayout(layoutInit);

		TaskCheckEmailAccount taskCheckAccount = new TaskCheckEmailAccount();
		taskCheckAccount.setCallback(new Callback()
		{
			@Override
			public void execute(NBTTagCompound nbt, boolean success)
			{
				if (success)
				{
					currentName = nbt.getString("Name");
					listEmails.removeAll();
					for (Email email : EmailManager.INSTANCE.inbox)
					{
						listEmails.addItem(email);
					}
					setCurrentLayout(layoutInbox);
				}
				else
				{
					btnRegisterAccount.setVisible(true);
					setCurrentLayout(layoutMainMenu);
				}
			}
		});
		TaskProxy.sendTask(taskCheckAccount);
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{
		
	}

	@Override
	public String getTitle()
	{
		if (getCurrentLayout() == layoutInbox)
		{
			return "Inbox: " + currentName + "@endermail.com";
		}
		if(getCurrentLayout() == layoutContacts)
		{
			return "Contacts";
		}
		return super.getDisplayName();
	}

	public static class EmailManager
	{

		public static final EmailManager INSTANCE = new EmailManager();

		@SideOnly(Side.CLIENT)
		private List<Email> inbox = new ArrayList<Email>();

		private Map<UUID, String> uuidToName = new HashMap<UUID, String>();
		private Map<String, List<Email>> uuidToInbox = new HashMap<String, List<Email>>();

		public boolean addEmailToInbox(Email email, String to)
		{
			if (uuidToInbox.containsKey(to))
			{
				uuidToInbox.get(to).add(0, email);
				return true;
			}
			return false;
		}

		@SideOnly(Side.CLIENT)
		public List<Email> getInbox()
		{
			return inbox;
		}

		public List<Email> getEmailsForAccount(EntityPlayer player)
		{
			if (uuidToName.containsKey(player.getUniqueID()))
			{
				return uuidToInbox.get(uuidToName.get(player.getUniqueID()));
			}
			return new ArrayList<Email>();
		}

		public boolean addAccount(EntityPlayer player, String name)
		{
			if (!uuidToName.containsKey(player.getUniqueID()))
			{
				if (!uuidToName.containsValue(name))
				{
					uuidToName.put(player.getUniqueID(), name);
					uuidToInbox.put(name, new ArrayList<Email>());
					return true;
				}
			}
			return false;
		}

		public boolean hasAccount(UUID uuid)
		{
			return uuidToName.containsKey(uuid);
		}

		public String getName(EntityPlayer player)
		{
			return uuidToName.get(player.getUniqueID());
		}

		public void readFromNBT(NBTTagCompound nbt)
		{
			uuidToInbox.clear();

			NBTTagList inboxes = (NBTTagList) nbt.getTag("Inboxes");
			for (int i = 0; i < inboxes.tagCount(); i++)
			{
				NBTTagCompound inbox = inboxes.getCompoundTagAt(i);
				String name = inbox.getString("Name");

				List<Email> emails = new ArrayList<Email>();
				NBTTagList emailTagList = (NBTTagList) inbox.getTag("Emails");
				for (int j = 0; j < emailTagList.tagCount(); j++)
				{
					NBTTagCompound emailTag = emailTagList.getCompoundTagAt(j);
					Email email = Email.readFromNBT(emailTag);
					emails.add(email);
				}
				uuidToInbox.put(name, emails);
			}

			uuidToName.clear();

			NBTTagList accounts = (NBTTagList) nbt.getTag("Accounts");
			for (int i = 0; i < accounts.tagCount(); i++)
			{
				NBTTagCompound account = accounts.getCompoundTagAt(i);
				UUID uuid = UUID.fromString(account.getString("UUID"));
				String name = account.getString("Name");
				uuidToName.put(uuid, name);
			}
		}

		public void writeToNBT(NBTTagCompound nbt)
		{
			NBTTagList inboxes = new NBTTagList();
			for (String key : uuidToInbox.keySet())
			{
				NBTTagCompound inbox = new NBTTagCompound();
				inbox.setString("Name", key);

				NBTTagList emailTagList = new NBTTagList();
				List<Email> emails = uuidToInbox.get(key);
				for (Email email : emails)
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
			for (UUID key : uuidToName.keySet())
			{
				NBTTagCompound account = new NBTTagCompound();
				account.setString("UUID", key.toString());
				account.setString("Name", uuidToName.get(key));
				accounts.appendTag(account);
			}
			nbt.setTag("Accounts", accounts);
		}

		public void clear()
		{
			uuidToInbox.clear();
			uuidToName.clear();
			inbox.clear();
		}
	}

	public static class Email
	{
		private String subject, author, message;
		private boolean read;

		public Email(String subject, String message)
		{
			this.subject = subject;
			this.message = message;
			this.read = false;
		}

		public Email(String subject, String author, String message)
		{
			this(subject, message);
			this.author = author;
		}

		public String getSubject()
		{
			return subject;
		}

		public String getAuthor()
		{
			return author;
		}

		public void setAuthor(String author)
		{
			this.author = author;
		}

		public String getMessage()
		{
			return message;
		}

		public boolean isRead()
		{
			return read;
		}

		public void setRead(boolean read)
		{
			this.read = read;
		}

		public void writeToNBT(NBTTagCompound nbt)
		{
			nbt.setString("subject", this.subject);
			if (author != null) nbt.setString("author", this.author);
			nbt.setString("message", this.message);
			nbt.setBoolean("read", this.read);
		}

		public static Email readFromNBT(NBTTagCompound nbt)
		{
			Email email = new Email(nbt.getString("subject"), nbt.getString("author"), nbt.getString("message"));
			email.setRead(nbt.getBoolean("read"));
			return email;
		}
	}
	
	private static class Contact
	{
		private String nickname;
		private String email;
		
		public Contact(String nickname, String email)
		{
			this.nickname = nickname;
			this.email = email;
		}
		
		public String getNickname()
		{
			return nickname;
		}
		
		public String getEmail()
		{
			return email;
		}
		
		@Override
		public String toString()
		{
			return nickname;
		}
	}
}
