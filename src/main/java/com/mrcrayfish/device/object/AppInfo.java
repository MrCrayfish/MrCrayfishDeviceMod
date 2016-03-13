package com.mrcrayfish.device.object;

public class AppInfo 
{
	private String name;
	
	public AppInfo(String name) 
	{
		this.name = name;
	}
	
	@Override
	public String toString() 
	{
		return name;
	}
}
