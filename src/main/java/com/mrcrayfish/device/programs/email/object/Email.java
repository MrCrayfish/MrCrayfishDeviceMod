package com.mrcrayfish.device.programs.email.object;

import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.programs.email.ApplicationEmail;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class Email
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
        if(author != null) nbt.setString("author", this.author);
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
