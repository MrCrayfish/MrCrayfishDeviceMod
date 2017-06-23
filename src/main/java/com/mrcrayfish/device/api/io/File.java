package com.mrcrayfish.device.api.io;

import com.mrcrayfish.device.api.app.Application;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

public class File
{
	public static final Comparator<File> SORT_BY_NAME = (f1, f2) -> {
		if(f1 instanceof Folder && !(f2 instanceof Folder)) return -1;
		if(!(f1 instanceof Folder) && f2 instanceof Folder) return 1;
		return f1.name.compareTo(f2.name);
	};

	protected Folder parent;
	protected String name;
	private String openingApp;
	private NBTTagCompound data;
	
	public File(String name, Application app, NBTTagCompound data) 
	{
		this(name, app.getID(), data);
	}
	
	public File(String name, String openingApp, NBTTagCompound data)
	{
		this.name = name;
		this.openingApp = openingApp;
		this.data = data;
	}
	
	protected File(String name)
	{
		this.name = name;
	}
	
	public String getName() 
	{
		return name;
	}

	@Nullable
	public String getOpeningApp() 
	{
		return openingApp;
	}
	
	public void setData(@Nonnull NBTTagCompound data)
	{
		if(data == null)
		{
			throw new DataException("A null compound tag cannot be set to a file");
		}
		this.data = data;
	}

	public NBTTagCompound getData() 
	{
		return data;
	}

	@Nullable
	public Folder getParent()
	{
		return parent;
	}
	
	public boolean isFolder()
	{
		return false;
	}

	public boolean isForApplication(Application app)
	{
		return openingApp != null && openingApp.equals(app.getID());
	}

	public boolean delete()
	{
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
		tag.setString("name", name);
		tag.setString("openingApp", openingApp);
		tag.setTag("data", data);
		return tag;
	}
	
	public static File fromTag(NBTTagCompound tag)
	{
		return new File(tag.getString("name"), tag.getString("openingApp"), tag.getCompoundTag("data"));
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj == null) return false;
		if(!(obj instanceof File)) return false;
		return ((File) obj).name.equalsIgnoreCase(name);
	}
	
	public File copy()
	{
		return new File(name, openingApp, data.copy());
	}
}
