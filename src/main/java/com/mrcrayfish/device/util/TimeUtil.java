package com.mrcrayfish.device.util;

/**
 * Contains useful methods to create times. 
 * 
 * <br>
 * </br>
 * 
 * <b>Author: MrCrayfish</b>
 */
public class TimeUtil
{
	/**
	 * Based on the long passed in will return a time based on the 12HR format.
	 * 
	 * @param time
	 *            The current time
	 * @return The time put into a string that humans can read
	 */
	public static String getTotalRealTime(long time)
	{
		int days = (int) (time / 1728000);
		int hours = (int) Math.floor(time / 24000.0) % 24;
		int minutes = (int) Math.floor(time / 1200) % 60;
		int seconds = (int) Math.floor(time / 20) % 60;
		if (days > 0)
		{
			return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
		} else if (hours > 0)
		{
			return String.format("%dh %dm %ds", hours, minutes, seconds);
		} else if (minutes > 0)
		{
			return String.format("%dm %ds", minutes, seconds);
		}
		return String.format("%ds", seconds);
	}

	/**
	 * Formats the ticks provided into a date and time.
	 * 
	 * @param ticks
	 *            The total world ticks
	 * @return The time put into a string that humans can read
	 */
	public static String getTotalGameTime(long ticks)
	{
		int days = (int) (ticks / 24000);
		int hours = (int) ((Math.floor(ticks / 1000.0) + 7) % 24);
		int minutes = (int) Math.floor((ticks % 1000) / 1000.0 * 60);
		return String.format("%02d:%02d", hours, minutes);
	}

	/**
	 * Formats the ticks provided into a time.
	 * 
	 * @param ticks
	 *            The current world ticks
	 * @return The time put into a string that humans can read
	 */
	public static String getGameTime(long ticks)
	{
		int hours = (int) ((Math.floor(ticks / 1000.0) + 7) % 24);
		int minutes = (int) Math.floor((ticks % 1000) / 1000.0 * 60);
		return String.format("%02d:%02d", hours, minutes);
	}

	/**
	 * Converts a real time into ticks.
	 * 
	 * @param hours
	 *            The amount of hours
	 * @param minutes
	 *            The amount of minutes
	 * @param seconds
	 *            The amount of seconds
	 * @return The amount of ticks that represents the time provided
	 */
	public static long getRealTimeToTicks(int hours, int minutes, int seconds)
	{
		return hours * 72000L + minutes * 1200L + seconds * 20L;
	}
}
