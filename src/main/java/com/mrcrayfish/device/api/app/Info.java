package com.mrcrayfish.device.api.app;

import net.minecraft.util.ResourceLocation;

public class Info 
{
	protected ResourceLocation icon;
	protected int u, v;

	protected final String APP_ID;
	protected final String DISPLAY_NAME;
	
	protected Info(String appId, String displayName) 
	{
		this.APP_ID = appId;
		this.DISPLAY_NAME = displayName;
	}
	
	protected void setIcon(ResourceLocation icon, int u, int v) 
	{
		this.icon = icon;
		this.u = u;
		this.v = v;
	}
	
	/**
	 * Gets the resource location the icon is located in
	 * 
	 * @return the icon resource location
	 */
	public ResourceLocation getIcon()
	{
		return icon;
	}

	/**
	 * Gets the u location of the icon
	 * 
	 * @return the u position
	 */
	public int getIconU()
	{
		return u;
	}

	/**
	 * Gets the v location of the icon
	 * 
	 * @return the v position
	 */
	public int getIconV()
	{
		return v;
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
