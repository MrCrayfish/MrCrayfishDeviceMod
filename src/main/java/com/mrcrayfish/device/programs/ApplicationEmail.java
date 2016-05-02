package com.mrcrayfish.device.programs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.ApplicationBar;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Layout;
import com.mrcrayfish.device.app.Layout.Background;
import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.Image;
import com.mrcrayfish.device.app.components.ItemList;
import com.mrcrayfish.device.app.components.Label;
import com.mrcrayfish.device.app.components.Spinner;
import com.mrcrayfish.device.app.components.Text;
import com.mrcrayfish.device.app.components.TextArea;
import com.mrcrayfish.device.app.components.TextField;
import com.mrcrayfish.device.app.listener.ClickListener;
import com.mrcrayfish.device.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.app.requests.TaskCheckEmailAccount;
import com.mrcrayfish.device.app.requests.TaskDeleteEmail;
import com.mrcrayfish.device.app.requests.TaskRegisterEmailAccount;
import com.mrcrayfish.device.app.requests.TaskSendEmail;
import com.mrcrayfish.device.app.requests.TaskUpdateInbox;
import com.mrcrayfish.device.app.requests.TaskViewEmail;
import com.mrcrayfish.device.task.Callback;
import com.mrcrayfish.device.task.TaskManager;

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

	/* New Email Layout */
	private Layout layoutViewEmail;
	private Label labelViewSubject;
	private Label labelSender;
	private Label labelFrom;
	private Label labelViewSubjectContent;
	private Label labelViewMessage;
	private Text textMessage;
	private Button btnCancelViewEmail;

	private String currentName;

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
		this.btnRegisterAccount.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				ApplicationEmail.this.setCurrentLayout(layoutRegisterAccount);
			}
		});
		this.btnRegisterAccount.setVisible(false);
		layoutMainMenu.addComponent(this.btnRegisterAccount);

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
				if (selected) gui.drawRect(x, y, x + width, y + getHeight(), Color.DARK_GRAY.getRGB());
				else gui.drawRect(x, y, x + width, y + getHeight(), Color.GRAY.getRGB());

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

		this.btnViewEmail = new Button(x, y, 5, 5, ENDER_MAIL_ICONS, 30, 0, 10, 10);
		this.btnViewEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				int index = listEmails.getSelectedIndex();
				if (index != -1)
				{
					TaskManager.sendRequest(new TaskViewEmail(index));
					Email email = listEmails.getSelectedItem();
					email.setRead(true);
					textMessage.setText(email.message);
					labelViewSubject.setText(email.subject);
					labelFrom.setText(email.author + "@endermail.com");
					setCurrentLayout(layoutViewEmail);
				}
			}
		});
		this.btnViewEmail.setToolTip("View", "Opens the currently selected email");
		layoutInbox.addComponent(this.btnViewEmail);

		this.btnNewEmail = new Button(x, y, 25, 5, ENDER_MAIL_ICONS, 0, 0, 10, 10);
		this.btnNewEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutNewEmail);
			}
		});
		this.btnNewEmail.setToolTip("New Email", "Send an email to a player");
		layoutInbox.addComponent(this.btnNewEmail);

		this.btnReplyEmail = new Button(x, y, 45, 5, ENDER_MAIL_ICONS, 60, 0, 10, 10);
		this.btnReplyEmail.setClickListener(new ClickListener()
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
		this.btnReplyEmail.setToolTip("Reply", "Reply to the currently selected email");
		layoutInbox.addComponent(this.btnReplyEmail);

		this.btnDeleteEmail = new Button(x, y, 65, 5, ENDER_MAIL_ICONS, 10, 0, 10, 10);
		this.btnDeleteEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				int index = listEmails.getSelectedIndex();
				if (index != -1)
				{
					TaskManager.sendRequest(new TaskDeleteEmail(index));
					listEmails.removeItem(index);
				}
			}
		});
		this.btnDeleteEmail.setToolTip("Trash Email", "Deletes the currently select email");
		layoutInbox.addComponent(this.btnDeleteEmail);

		this.btnRefresh = new Button(x, y, 85, 5, ENDER_MAIL_ICONS, 20, 0, 10, 10);
		this.btnRefresh.setClickListener(new ClickListener()
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
				TaskManager.sendRequest(taskUpdateInbox);
			}
		});
		this.btnRefresh.setToolTip("Refresh Inbox", "Checks for any new emails");
		layoutInbox.addComponent(this.btnRefresh);

		this.layoutNewEmail = new Layout(255, 148);

		this.labelTo = new Label("To", x, y, 5, 8);
		layoutNewEmail.addComponent(this.labelTo);

		this.fieldRecipient = new TextField(Minecraft.getMinecraft().fontRendererObj, x, y, 50, 5, 200);
		layoutNewEmail.addComponent(this.fieldRecipient);

		this.labelSubject = new Label("Subject", x, y, 5, 26);
		layoutNewEmail.addComponent(this.labelSubject);

		this.fieldSubject = new TextField(Minecraft.getMinecraft().fontRendererObj, x, y, 50, 23, 200);
		layoutNewEmail.addComponent(this.fieldSubject);

		this.labelMessage = new Label("Message", x, y, 5, 44);
		layoutNewEmail.addComponent(this.labelMessage);

		this.textAreaMessage = new TextArea(Minecraft.getMinecraft().fontRendererObj, x, y, 50, 41, 200, 100);
		layoutNewEmail.addComponent(this.textAreaMessage);

		this.btnSendEmail = new Button(x, y, 6, 60, ENDER_MAIL_ICONS, 50, 0, 10, 10);
		this.btnSendEmail.setClickListener(new ClickListener()
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
				TaskManager.sendRequest(taskSendEmail);
			}
		});
		this.btnSendEmail.setToolTip("Send", "Send email to recipient");
		layoutNewEmail.addComponent(this.btnSendEmail);

		this.btnCancelEmail = new Button(x, y, 28, 60, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		this.btnCancelEmail.setClickListener(new ClickListener()
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
		this.btnCancelEmail.setToolTip("Cancel", "Go back to Inbox");
		layoutNewEmail.addComponent(this.btnCancelEmail);

		this.layoutViewEmail = new Layout(240, 156);
		this.layoutViewEmail.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y)
			{
				gui.drawRect(x, y + 22, x + layoutViewEmail.width, y + 50, Color.GRAY.getRGB());
				gui.drawRect(x, y + 22, x + layoutViewEmail.width, y + 23, Color.DARK_GRAY.getRGB());
				gui.drawRect(x, y + 49, x + layoutViewEmail.width, y + 50, Color.DARK_GRAY.getRGB());
				gui.drawRect(x, y + 50, x + layoutViewEmail.width, y + 156, COLOR_EMAIL_CONTENT_BACKGROUND.getRGB());
			}
		});

		this.labelViewSubject = new Label("Subject", x, y, 5, 26);
		this.labelViewSubject.setTextColour(new Color(255, 170, 0));
		layoutViewEmail.addComponent(this.labelViewSubject);

		this.labelFrom = new Label("From", x, y, 5, 38);
		layoutViewEmail.addComponent(labelFrom);

		this.btnCancelViewEmail = new Button(x, y, 5, 3, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		this.btnCancelViewEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutInbox);
			}
		});
		this.btnCancelViewEmail.setToolTip("Cancel", "Go back to Inbox");
		layoutViewEmail.addComponent(this.btnCancelViewEmail);

		this.textMessage = new Text("Hallo", Minecraft.getMinecraft().fontRendererObj, x, y, 5, 54, 230);
		this.textMessage.setShadow(false);
		layoutViewEmail.addComponent(this.textMessage);

		this.setCurrentLayout(layoutInit);

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
					ApplicationEmail.this.setCurrentLayout(layoutInbox);
				}
				else
				{
					btnRegisterAccount.setVisible(true);
					ApplicationEmail.this.setCurrentLayout(layoutMainMenu);
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

	@Override
	public String getTitle()
	{
		if (getCurrentLayout() == this.layoutInbox)
		{
			return "Inbox: " + currentName + "@endermail.com";
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
}
