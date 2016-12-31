package com.mrcrayfish.device.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Info;

public class ApplicationManager
{
	private static final List<Info> APPS = new ArrayList<Info>();
	
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
	public static ImmutableList<Info> getApps()
	{
		return ImmutableList.<Info>builder().addAll(APPS.iterator()).build();
	}
	
	public static Application getApp(String appId)
	{
		for(Info info : APPS)
		{
			if(info.getID().equals(appId))
			{
				return (Application) info;
			}
		}
		return null;
	}
}
