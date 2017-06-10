package com.mrcrayfish.device.api.app;

import net.minecraft.util.ResourceLocation;

public class Info 
{
	protected Application.Icon icon;

	protected final String APP_ID;
	protected final String DISPLAY_NAME;
	
	protected Info(String appId, String displayName) 
	{
		this.APP_ID = appId;
		this.DISPLAY_NAME = displayName;
	}

	protected void setIcon(ResourceLocation resource, int u, int v)
	{
		this.icon = new Application.Icon(resource, u, v);
	}

	/**
	 * Gets the resource location the icon is located in
	 *
	 * @return the icon resource location
	 */
	public Application.Icon getIcon()
	{
		return icon;
	}

	/**
	 * Gets the id of this application
	 * 
	 * @return the applicaiton id
	 */
	public final String getID()
	{
		return APP_ID;
	}
	
	/**
	 * Gets the name that is displayed in the task bar.
	 * 
	 * @return the display name
	 */
	public final String getDisplayName()
	{
		return DISPLAY_NAME;
	}
}
