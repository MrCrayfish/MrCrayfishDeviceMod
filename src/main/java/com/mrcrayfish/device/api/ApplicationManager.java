package com.mrcrayfish.device.api;

import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.api.app.Application;

public class ApplicationManager
{
	public static final List<Application> APPS = new ArrayList<Application>();
	
	/**
	 * Registers an application into the application list
	 * 
	 * @param app the application you want to register
	 */
	public static void registerApplication(Application app)
	{
		if(app != null)
		{
			APPS.add(app);
		}
	}
	
	/**
	 * Get all applications that are registered. Please note
	 * that this list is read only and cannot be modified.
	 * 
	 * @return the application list
	 */
}
