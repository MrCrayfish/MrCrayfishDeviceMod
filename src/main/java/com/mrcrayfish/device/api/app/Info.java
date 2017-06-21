package com.mrcrayfish.device.api.app;

public interface Info
{
	/**
	 * Gets the resource location the icon is located in
	 *
	 * @return the icon resource location
	 */
	Application.Icon getIcon();

	/**
	 * Gets the id of this application
	 * 
	 * @return the applicaiton id
	 */
	String getID();
	
	/**
	 * Gets the name that is displayed in the task bar.
	 * 
	 * @return the display name
	 */
	String getDisplayName();
}
