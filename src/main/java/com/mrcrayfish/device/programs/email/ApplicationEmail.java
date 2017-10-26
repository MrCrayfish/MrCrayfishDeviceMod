package com.mrcrayfish.device.programs.email;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.InitListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.email.task.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private TextField fieldRecipient;
	private TextField fieldSubject;
	private TextArea textAreaMessage;
	private Button btnSendEmail;
	private Button btnCancelEmail;
	private Button btnAttachedFile;
	private Button btnRemoveAttachedFile;
	private Label labelAttachedFile;

	/* View Email Layout */
	private Layout layoutViewEmail;
	private Label labelViewSubject;
	private Label labelSender;
	private Label labelFrom;
	private Label labelViewSubjectContent;
	private Label labelViewMessage;
	private Text textMessage;
	private Button btnCancelViewEmail;
	private Button btnSaveAttachment;
	private Label labelAttachmentName;
	
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
	private File attachedFile;
	
	private List<Contact> contacts;

	@Override
	public void init()
	{
		/* Loading Layout */
		layoutInit = new Layout(40, 40);

		spinnerInit = new Spinner(14, 10);
		layoutInit.addComponent(spinnerInit);

		labelLoading = new Label("Loading...", 2, 26);
		layoutInit.addComponent(labelLoading);

		
		/* Main Menu Layout */
		
		layoutMainMenu = new Layout(100, 75);

		logo = new Image(35, 5, 28, 28, info.getIconU(), info.getIconV(), 14, 14, Laptop.ICON_TEXTURES);
		layoutMainMenu.addComponent(logo);

		labelLogo = new Label("Ender Mail", 50, 35);
		labelLogo.setAlignment(Component.ALIGN_CENTER);
		layoutMainMenu.addComponent(labelLogo);

		btnRegisterAccount = new Button(5, 50, "Register");
		btnRegisterAccount.setSize(90, 20);
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

		btnRegister = new Button(5, 35, "Register");
		btnRegister.setSize(157, 20);
		btnRegister.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				int length = fieldEmail.getText().length();
				if (length > 0 && length <= 10)
				{
					TaskRegisterEmailAccount taskRegisterAccount = new TaskRegisterEmailAccount(fieldEmail.getText());
					taskRegisterAccount.setCallback((nbt, success) ->
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
                    });
					TaskManager.sendTask(taskRegisterAccount);
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
				taskUpdateInbox.setCallback((nbt, success) ->
				{
                    listEmails.removeAll();
                    for (Email email : EmailManager.INSTANCE.getInbox())
                    {
                        listEmails.addItem(email);
                    }
                });
				TaskManager.sendTask(taskUpdateInbox);
			}
		});

		listEmails = new ItemList<Email>(5, 25, 275, 4);
		listEmails.setListItemRenderer(new ListItemRenderer<Email>(28)
		{
			@Override
			public void render(Email e, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				Gui.drawRect(x, y, x + width, y + height, selected ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());

				if (!e.isRead())
				{
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					mc.getTextureManager().bindTexture(ENDER_MAIL_ICONS);
					gui.drawTexturedModalRect(x + 247, y + 8, 0, 10, 20, 12);
				}

				if(e.attachment != null)
				{
					GlStateManager.color(1.0F, 1.0F, 1.0F);
					int posX = x + (!e.isRead() ? -30 : 0) + 255;
					mc.getTextureManager().bindTexture(ENDER_MAIL_ICONS);
					gui.drawTexturedModalRect(posX, y + 5, 20, 10, 13, 20);
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
					TaskManager.sendTask(new TaskViewEmail(index));
					Email email = listEmails.getSelectedItem();
					email.setRead(true);
					textMessage.setText(email.message);
					labelViewSubject.setText(email.subject);
					labelFrom.setText(email.author + "@endermail.com");
					attachedFile = email.getAttachment();
					if(attachedFile != null)
					{
						btnSaveAttachment.setVisible(true);
						labelAttachmentName.setVisible(true);
						labelAttachmentName.setText(attachedFile.getName());
					}
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
					taskDeleteEmail.setCallback((nbt, success) ->
					{
                        listEmails.removeItem(index);
                        EmailManager.INSTANCE.getInbox().remove(index);
                    });
					TaskManager.sendTask(taskDeleteEmail);
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
				taskUpdateInbox.setCallback((nbt, success) ->
				{
                    listEmails.removeAll();
                    for (Email email : EmailManager.INSTANCE.getInbox())
                    {
                        listEmails.addItem(email);
                    }
                });
				TaskManager.sendTask(taskUpdateInbox);
			}
		});
		btnRefresh.setToolTip("Refresh Inbox", "Checks for any new emails");
		layoutInbox.addComponent(btnRefresh);

		
		/* New Email Layout */
		
		layoutNewEmail = new Layout(231, 148);
		layoutNewEmail.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			if(attachedFile != null)
			{
				AppInfo info = ApplicationManager.getApplication(attachedFile.getOpeningApp());
				RenderUtil.drawApplicationIcon(info, x + 46, y + 130);
			}
        });

		fieldRecipient = new TextField(26, 5, 200);
		fieldRecipient.setPlaceholder("To");
		layoutNewEmail.addComponent(fieldRecipient);

		fieldSubject = new TextField(26, 23, 200);
		fieldSubject.setPlaceholder("Subject");
		layoutNewEmail.addComponent(fieldSubject);

		textAreaMessage = new TextArea(26, 41, 200, 85);
		textAreaMessage.setPlaceholder("Message");
		layoutNewEmail.addComponent(textAreaMessage);

		btnSendEmail = new Button(5, 5, ENDER_MAIL_ICONS, 50, 0, 10, 10);
		btnSendEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				Matcher matcher = EMAIL.matcher(fieldRecipient.getText());
				if (!matcher.matches()) return;

				Email email = new Email(fieldSubject.getText(), textAreaMessage.getText(), attachedFile);
				TaskSendEmail taskSendEmail = new TaskSendEmail(email, matcher.group(1));
				taskSendEmail.setCallback((nbt, success) ->
				{
                    if (success)
                    {
                        setCurrentLayout(layoutInbox);
                        textAreaMessage.clear();
                        fieldSubject.clear();
                        fieldRecipient.clear();
						resetAttachedFile();
                    }
                });
				TaskManager.sendTask(taskSendEmail);
			}
		});
		btnSendEmail.setToolTip("Send", "Send email to recipient");
		layoutNewEmail.addComponent(btnSendEmail);

		btnCancelEmail = new Button(5, 25, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		btnCancelEmail.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutInbox);
				textAreaMessage.clear();
				fieldSubject.clear();
				fieldRecipient.clear();
				resetAttachedFile();
			}
		});
		btnCancelEmail.setToolTip("Cancel", "Go back to Inbox");
		layoutNewEmail.addComponent(btnCancelEmail);

		btnAttachedFile = new Button(26, 129, ENDER_MAIL_ICONS, 70, 0, 10, 10);
		btnAttachedFile.setToolTip("Attach File", "Select a file from computer to attach to this email");
		btnAttachedFile.setClickListener((c, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				Dialog.OpenFile dialog = new Dialog.OpenFile(this);
				dialog.setResponseHandler((success, file) ->
				{
					if(!file.isFolder())
					{
						attachedFile = file.copy();
						labelAttachedFile.setText(file.getName());
						labelAttachedFile.left += 16;
						labelAttachedFile.xPosition += 16;
						btnAttachedFile.setVisible(false);
						btnRemoveAttachedFile.setVisible(true);
						dialog.close();
					}
					else
					{
						openDialog(new Dialog.Message("Attachment must be a file!"));
					}
					return false;
				});
				openDialog(dialog);
			}
        });
		layoutNewEmail.addComponent(btnAttachedFile);

		btnRemoveAttachedFile = new Button(26, 129, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		btnRemoveAttachedFile.setToolTip("Remove Attachment", "Delete the attached file from this email");
		btnRemoveAttachedFile.setVisible(false);
		btnRemoveAttachedFile.setClickListener((c, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				resetAttachedFile();
			}
        });
		layoutNewEmail.addComponent(btnRemoveAttachedFile);

		labelAttachedFile = new Label("No file attached", 46, 133);
		layoutNewEmail.addComponent(labelAttachedFile);
		
		
		/* View Email Layout */
		
		layoutViewEmail = new Layout(240, 156);
		layoutViewEmail.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
            Gui.drawRect(x, y + 22, x + layoutViewEmail.width, y + 50, Color.GRAY.getRGB());
            Gui.drawRect(x, y + 22, x + layoutViewEmail.width, y + 23, Color.DARK_GRAY.getRGB());
            Gui.drawRect(x, y + 49, x + layoutViewEmail.width, y + 50, Color.DARK_GRAY.getRGB());
            Gui.drawRect(x, y + 50, x + layoutViewEmail.width, y + 156, COLOR_EMAIL_CONTENT_BACKGROUND.getRGB());

			if(attachedFile != null)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				AppInfo info = ApplicationManager.getApplication(attachedFile.getOpeningApp());
				RenderUtil.drawApplicationIcon(info, x + 204, y + 4);
			}
		});

		labelViewSubject = new Label("Subject", 5, 26);
		labelViewSubject.setTextColour(new Color(255, 170, 0));
		layoutViewEmail.addComponent(labelViewSubject);

		labelFrom = new Label("From", 5, 38);
		layoutViewEmail.addComponent(labelFrom);

		btnCancelViewEmail = new Button(5, 3, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		btnCancelViewEmail.setClickListener((c, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				attachedFile = null;
				btnSaveAttachment.setVisible(false);
				labelAttachmentName.setVisible(false);
				setCurrentLayout(layoutInbox);
			}

		});
		btnCancelViewEmail.setToolTip("Cancel", "Go back to Inbox");
		layoutViewEmail.addComponent(btnCancelViewEmail);

		textMessage = new Text("Hallo", 5, 54, 230);
		textMessage.setShadow(false);
		layoutViewEmail.addComponent(textMessage);

		btnSaveAttachment = new Button(219, 3, ENDER_MAIL_ICONS, 80, 0, 10, 10);
		btnSaveAttachment.setToolTip("Save Attachment", "Save the file attached to this email");
		btnSaveAttachment.setVisible(false);
		btnSaveAttachment.setClickListener((c, mouseButton) ->
		{
            if(mouseButton == 0 && attachedFile != null)
            {
            	Dialog.SaveFile dialog = new Dialog.SaveFile(this, attachedFile);
            	openDialog(dialog);
			}
        });
		layoutViewEmail.addComponent(btnSaveAttachment);

		labelAttachmentName = new Label("", 200, 7);
		labelAttachmentName.setVisible(false);
		labelAttachmentName.setAlignment(Component.ALIGN_RIGHT);
		layoutViewEmail.addComponent(labelAttachmentName);

		setCurrentLayout(layoutInit);

		TaskCheckEmailAccount taskCheckAccount = new TaskCheckEmailAccount();
		taskCheckAccount.setCallback((nbt, success) ->
		{
            if (success)
            {
                currentName = nbt.getString("Name");
                listEmails.removeAll();
                for (Email email : EmailManager.INSTANCE.getInbox())
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
        });
		TaskManager.sendTask(taskCheckAccount);
	}

	private void resetAttachedFile()
	{
		if(attachedFile != null)
		{
			labelAttachedFile.setText("No file attached");
			labelAttachedFile.left -= 16;
			labelAttachedFile.xPosition -= 16;
			btnRemoveAttachedFile.setVisible(false);
			btnAttachedFile.setVisible(true);
			attachedFile = null;
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

	@Override
	public String getWindowTitle()
	{
		if (getCurrentLayout() == layoutInbox)
		{
			return "Inbox: " + currentName + "@endermail.com";
		}
		if(getCurrentLayout() == layoutContacts)
		{
			return "Contacts";
		}
		return info.getName();
	}

	@Override
	public void onClose()
	{
		super.onClose();
		attachedFile = null;
	}

	public static class EmailManager
	{
		public static final EmailManager INSTANCE = new EmailManager();

		@SideOnly(Side.CLIENT)
		private List<Email> inbox;

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
			if(inbox == null)
			{
				inbox = new ArrayList<>();
			}
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
		private File attachment;
		private boolean read;

		public Email(String subject, String message, @Nullable File file)
		{
			this.subject = subject;
			this.message = message;
			this.attachment = file;
			this.read = false;
		}

		public Email(String subject, String author, String message, @Nullable File attachment)
		{
			this(subject, message, attachment);
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

		public File getAttachment()
		{
			return attachment;
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

			if(attachment != null)
			{
				NBTTagCompound fileTag = new NBTTagCompound();
				fileTag.setString("file_name", attachment.getName());
				fileTag.setTag("data", attachment.toTag());
				nbt.setTag("attachment", fileTag);
			}
		}

		public static Email readFromNBT(NBTTagCompound nbt)
		{
			File attachment = null;
			if(nbt.hasKey("attachment", Constants.NBT.TAG_COMPOUND))
			{
				NBTTagCompound fileTag = nbt.getCompoundTag("attachment");
				attachment = File.fromTag(fileTag.getString("file_name"), fileTag.getCompoundTag("data"));
			}
			Email email = new Email(nbt.getString("subject"), nbt.getString("author"), nbt.getString("message"), attachment);
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
