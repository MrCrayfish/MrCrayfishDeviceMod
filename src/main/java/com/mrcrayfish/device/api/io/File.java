package com.mrcrayfish.device.api.io;

import com.mrcrayfish.device.api.app.Application;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.regex.Pattern;

public class File
{
	public static final Pattern PATTERN_FILE_NAME = Pattern.compile("^[\\w. ]{1,32}$");

	public static final Comparator<File> SORT_BY_NAME = (f1, f2) -> {
		if(f1 instanceof Folder && !(f2 instanceof Folder)) return -1;
		if(!(f1 instanceof Folder) && f2 instanceof Folder) return 1;
		return f1.name.compareTo(f2.name);
	};

	protected Folder parent;
	protected String name;
	protected String openingApp;
	protected NBTTagCompound data;
	protected boolean protect = false;

	protected File() {}

	public File(String name, Application app, NBTTagCompound data) 
	{
		this(name, app.getInfo().getFormattedId(), data, false);
	}
	
	public File(String name, String openingAppId, NBTTagCompound data)
	{
		this(name, openingAppId, data, false);
	}

	private File(String name, String openingAppId, NBTTagCompound data, boolean protect)
	{
		if(!PATTERN_FILE_NAME.matcher(name).matches())
			throw new IllegalArgumentException("Invalid file name. The name must match the regular expression: ^[a-zA-Z0-9_ ]{1,16}$");

		this.name = name;
		this.openingApp = openingAppId;
		this.data = data;
		this.protect = protect;
	}
	
	public String getName() 
	{
		return name;
	}

	public boolean rename(String name)
	{
		if(this.protect)
			return false;
		this.name = name;
		return true;
	}

	@Nullable
	public String getOpeningApp() 
	{
		return openingApp;
	}
	
	public void setData(@Nonnull NBTTagCompound data)
	{
		if(this.protect)
			return;
		this.data = data;
	}

	@Nullable
	public NBTTagCompound getData() 
	{
		return data;
	}

	@Nullable
	public Folder getParent()
	{
		return parent;
	}

	public boolean isProtected()
	{
		return protect;
	}
	
	public boolean isFolder()
	{
		return false;
	}

	public boolean isForApplication(Application app)
	{
		return openingApp != null && openingApp.equals(app.getInfo().getFormattedId());
	}

	public boolean delete()
	{
		if(this.protect)
			return false;
		if(parent != null)
		{
			parent.delete(this);
			return true;
		}
		return false;
	}
	
	public NBTTagCompound toTag() 
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("openingApp", openingApp);
		tag.setTag("data", data);
		return tag;
	}
	
	public static File fromTag(String name, NBTTagCompound tag)
	{
		return new File(name, tag.getString("openingApp"), tag.getCompoundTag("data"));
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj == null)
			return false;
		if(!(obj instanceof File))
			return false;
		return ((File) obj).name.equalsIgnoreCase(name);
	}
	
	public File copy()
	{
		return new File(name, openingApp, data.copy());
	}
}
