package com.mrcrayfish.device.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;

import net.minecraft.util.ResourceLocation;

/**
 * Manages all the applications that can be added to the {@link Laptop} in the device mod. Can be used to register an application with {@link #registerApplication(ResourceLocation, Class)} or to get certain applications.
 * 
 * <br>
 * </br>
 * 
 * <b>Author: MrCrayfish</b>
 */
public final class ApplicationManager
{
	private static final Map<ResourceLocation, AppInfo> APP_INFO = new HashMap<>();

	private ApplicationManager()
	{
	}

	/**
	 * Registers an application into the application list
	 * <p>
	 * Example: {@code new ResourceLocation("modid", "appid");}
	 *
	 * @param identifier
	 *            simply just an id for the application.
	 * @param clazz
	 *            The class of the application
	 */
	@Nullable
	public static Application registerApplication(ResourceLocation identifier, Class<? extends Application> clazz)
	{
		Application application = MrCrayfishDeviceMod.proxy.registerApplication(identifier, clazz);
		if (application != null)
		{
			APP_INFO.put(identifier, application.getInfo());
			return application;
		}
		return null;
	}

	/**
	 * Get all applications that are registered. The returned collection does not include system applications, see {@link #getSystemApplications()} or {@link #getAllApplications()}. Please note that this list is read only and cannot be modified.
	 *
	 * @return the application list
	 */
	public static List<AppInfo> getAvailableApplications()
	{
		final Predicate<AppInfo> FILTER = info -> !info.isSystemApp() && (!MrCrayfishDeviceMod.proxy.hasAllowedApplications() || MrCrayfishDeviceMod.proxy.getAllowedApplications().contains(info));
		return APP_INFO.values().stream().filter(FILTER).collect(Collectors.toList());
	}

	/**
	 * Get all applications that are registered as system apps. The returned collection only includes system applications. Please note that this list is read only and cannot be modified.
	 *
	 * @return the application list
	 */
	public static List<AppInfo> getSystemApplications()
	{
		return APP_INFO.values().stream().filter(AppInfo::isSystemApp).collect(Collectors.toList());
	}

	/**
	 * Get all applications. Please note that this list is read only and cannot be modified.
	 *
	 * @return the application list
	 */
	public static List<AppInfo> getAllApplications()
	{
		return new ArrayList<>(APP_INFO.values());
	}

	/**
	 * Checks all the apps registered for a matching app or null if one was not found.
	 * 
	 * @param appId
	 *            The id of the app. Example {@code #getApplication("modid.appid")}
	 * @return The application info that was found with that id or null of there was not one registered to that id
	 */
	@Nullable
	public static AppInfo getApplication(String appId)
	{
		return APP_INFO.get(new ResourceLocation(appId.replace(".", ":")));
	}
}
