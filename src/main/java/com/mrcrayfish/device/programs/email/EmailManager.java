package com.mrcrayfish.device.programs.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mrcrayfish.device.api.app.Database;
import com.mrcrayfish.device.programs.email.object.Email;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class EmailManager implements Database
{
	public static final EmailManager INSTANCE = new EmailManager();

	private Map<UUID, String> uuidToAddress = new HashMap<UUID, String>();
	private Map<String, List<Email>> addressToInbox = new HashMap<String, List<Email>>();
	
	private EmailManager() {}

	public boolean addEmailToInbox(Email email, String to)
	{
		if (addressToInbox.containsKey(to))
		{
			addressToInbox.get(to).add(0, email);
			return true;
		}
		return false;
	}

	public List<Email> getInbox(EntityPlayer player)
	{
		if (uuidToAddress.containsKey(player.getUniqueID()))
		{
			return addressToInbox.get(uuidToAddress.get(player.getUniqueID()));
		}
		return new ArrayList<Email>();
	}

	public boolean addAccount(EntityPlayer player, String address)
	{
		if (!uuidToAddress.containsKey(player.getUniqueID()))
		{
			if (!uuidToAddress.containsValue(address))
			{
				uuidToAddress.put(player.getUniqueID(), address);
				addressToInbox.put(address, new ArrayList<Email>());
				return true;
			}
		}
		return false;
	}

	public boolean hasAccount(UUID uuid)
	{
		return uuidToAddress.containsKey(uuid);
	}

	public String getAddress(EntityPlayer player)
	{
		return uuidToAddress.get(player.getUniqueID());
	}

	public void clear()
	{
		addressToInbox.clear();
		uuidToAddress.clear();
	}

	@Override
	public String getName()
	{
		return "inboxes";
	}

	@Override
	public void save(NBTTagCompound tag)
	{
		NBTTagList inboxes = new NBTTagList();
		for (String key : addressToInbox.keySet())
		{
			NBTTagCompound inbox = new NBTTagCompound();
			inbox.setString("Name", key);

			NBTTagList emailTagList = new NBTTagList();
			List<Email> emails = addressToInbox.get(key);
			for (Email email : emails)
			{
				NBTTagCompound emailTag = new NBTTagCompound();
				email.writeToNBT(emailTag);
				emailTagList.appendTag(emailTag);
			}
			inbox.setTag("Emails", emailTagList);
			inboxes.appendTag(inbox);
		}
		tag.setTag("Inboxes", inboxes);

		NBTTagList accounts = new NBTTagList();
		for (UUID key : uuidToAddress.keySet())
		{
			NBTTagCompound account = new NBTTagCompound();
			account.setString("UUID", key.toString());
			account.setString("Name", uuidToAddress.get(key));
			accounts.appendTag(account);
		}
		tag.setTag("Accounts", accounts);
	}

	@Override
	public void load(NBTTagCompound tag)
	{
		addressToInbox.clear();

		NBTTagList inboxes = (NBTTagList) tag.getTag("Inboxes");
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
			addressToInbox.put(name, emails);
		}

		uuidToAddress.clear();

		NBTTagList accounts = (NBTTagList) tag.getTag("Accounts");
		for (int i = 0; i < accounts.tagCount(); i++)
		{
			NBTTagCompound account = accounts.getCompoundTagAt(i);
			UUID uuid = UUID.fromString(account.getString("UUID"));
			String name = account.getString("Name");
			uuidToAddress.put(uuid, name);
		}
	}
}