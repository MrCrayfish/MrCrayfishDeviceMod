package com.mrcrayfish.device.programs.email;

import com.google.common.collect.HashBiMap;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.programs.email.object.Email;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

/**
 * Author: MrCrayfish
 */
public class EmailManager
{
    public static final EmailManager INSTANCE = new EmailManager();

    @SideOnly(Side.CLIENT)
    private List<Email> inbox;

    private HashBiMap<UUID, String> uuidToName = HashBiMap.create();
    private Map<String, List<Email>> nameToInbox = new HashMap<>();

    public boolean addEmailToInbox(Email email, String to)
    {
        if (nameToInbox.containsKey(to))
        {
            nameToInbox.get(to).add(0, email);
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
            return nameToInbox.get(uuidToName.get(player.getUniqueID()));
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
                nameToInbox.put(name, new ArrayList<Email>());
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
        nameToInbox.clear();

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
            nameToInbox.put(name, emails);
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
        for (String key : nameToInbox.keySet())
        {
            NBTTagCompound inbox = new NBTTagCompound();
            inbox.setString("Name", key);

            NBTTagList emailTagList = new NBTTagList();
            List<Email> emails = nameToInbox.get(key);
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
        nameToInbox.clear();
        uuidToName.clear();
        inbox.clear();
    }

}
