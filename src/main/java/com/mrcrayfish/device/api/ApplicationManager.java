package com.mrcrayfish.device.api;

import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.api.app.Application;

public class ApplicationManager
{
	public static final List<Application> APPS = new ArrayList<Application>();
	
	public static void registerApplication(Application app)
	{
		if(app != null)
		{
			APPS.add(app);
		}
	}
}
