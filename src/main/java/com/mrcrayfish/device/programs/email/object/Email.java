package com.mrcrayfish.device.programs.email.object;

import net.minecraft.nbt.NBTTagCompound;

public class Email
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