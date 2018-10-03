package com.mrcrayfish.device.util;

public class TimeUtil
{
	public static String getTotalRealTime(long ticks)
	{
		int days = (int) (ticks / 1728000);
		int hours = (int) Math.floor(ticks / 24000.0) % 24;
	    int minutes = (int) Math.floor(ticks / 1200) % 60;
	    int seconds = (int) Math.floor(ticks / 20) % 60;
	    if(days > 0) 
	    {
	    	return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
	    } 
	    else if(hours > 0) 
	    {
	    	return String.format("%dh %dm %ds", hours, minutes, seconds);
	    }
	    else if(minutes > 0)
	    {
	    	return String.format("%dm %ds", minutes, seconds);
	    }
	    return String.format("%ds", seconds);
	}
	
	public static String getTotalGameTime(long ticks)
	{
		int days = (int) (ticks / 24000);
		int hours = (int) ((Math.floor(ticks / 1000.0) + 7) % 24);
	    int minutes = (int) Math.floor((ticks % 1000) / 1000.0 * 60);
	    return String.format("%02d:%02d", hours, minutes);
	}
	
	public static String getGameTime(long ticks)
	{
		int hours = (int) ((Math.floor(ticks / 1000.0) + 7) % 24);
	    int minutes = (int) Math.floor((ticks % 1000) / 1000.0 * 60);
	    return String.format("%02d:%02d", hours, minutes);
	}
	
	public static long getRealTimeToTicks(int hours, int minutes, int seconds)
	{
		return hours * 72000L + minutes * 1200L + seconds * 20L;
	}
}
