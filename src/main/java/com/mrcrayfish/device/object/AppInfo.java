package com.mrcrayfish.device.object;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class AppInfo 
{
	private final ResourceLocation APP_ID;
	private int iconU = 0, iconV = 0;

	public AppInfo(ResourceLocation identifier)
	{
		this.APP_ID = identifier;
	}

	/**
	 * Gets the id of the application
	 *
	 * @return the app resource location
	 */
	public ResourceLocation getId()
	{
		return APP_ID;
	}

	/**
	 * Gets the formatted version of the application's id
	 *
	 * @return a formatted id
	 */
	public String getFormattedId()
	{
		return APP_ID.getResourceDomain() + "." + APP_ID.getResourcePath();
	}

	/**
	 * Gets the name of the application
	 *
	 * @return the application name
	 */
	public String getName() 
	{
		return I18n.format("app." + this.getFormattedId() + ".name");
	}
	
	public String getAuthor() 
	{
		return I18n.format("app." + this.getFormattedId() + ".author");
	}
	
	public String getDescription() 
	{
		return I18n.format("app." + this.getFormattedId() + ".desc");
	}

	public int getIconU()
	{
		return iconU;
	}

	public int getIconV()
	{
		return iconV;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		if(!(obj instanceof AppInfo)) return false;
		AppInfo info = (AppInfo) obj;
		return this == info || getFormattedId().equals(info.getFormattedId());
	}
}
