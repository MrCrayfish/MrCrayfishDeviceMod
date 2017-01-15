package com.mrcrayfish.device.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mrcrayfish.device.api.app.Application;

public class ApplicationManager
{
	private static final List<Application> APPS = new ArrayList<Application>();
	private static final List<Application> READ_ONLY_APPS = Collections.unmodifiableList(APPS);
	
	/**
	 * Registers an application into the application list
	 * 
	 * @param app the application you want to register
	 */
	public static void registerApplication(Application app)
	{
		if(app != null)
		{
			if(!APPS.contains(app))
			{
				APPS.add(app);
			}
			else
			{
				throw new RuntimeException("An application with the id '" + app.getID() + "' has already been registered.");
			}
		}
	}
	
	/**
	 * Get all applications that are registered. Please note
	 * that this list is read only and cannot be modified.
	 * 
	 * @return the application list
	 */
	public static List<Application> getApps()
	{
		return READ_ONLY_APPS;
	}
}
