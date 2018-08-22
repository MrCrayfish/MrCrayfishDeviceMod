package com.mrcrayfish.device;

/**
 * An exception that is used if the user tries to load the mod while {@link MrCrayfishDeviceMod#DEVELOPER_MODE} is true and the mod is not running in a deobfuscated environment.01
 * 
 * <br>
 * </br>
 * 
 * <b>Author: MrCrayfish</b>
 */
public class LaunchException extends Exception
{

	@Override
	public String getMessage()
	{
		return "The developer version of the Device Mod has been detected and can only be run in a Forge development " + "environment. If you are not a developer, download the normal version (https://mrcrayfish.com/mods?id=cdm)";
	}
}