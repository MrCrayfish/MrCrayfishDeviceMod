package com.mrcrayfish.device.api;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.registry.BasicContainer;
import com.mrcrayfish.device.object.AppInfo;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ApplicationManager
{
	private static final Map<ResourceLocation, AppInfo> APP_INFO = new HashMap<>();

	private ApplicationManager() {}

	/**
	 * Registers an application into the application list
	 *
	 * The identifier parameter is simply just an id for the application.
	 * <p>
	 * Example: {@code new ResourceLocation("modid:appid");}
	 *
	 * @param identifier the ID of the application. A resource location
	 * @param clazz the application class being registered
	 */
	@Nullable
	public static Application registerApplication(ResourceLocation identifier, Class<? extends Application> clazz)
	{
		Application application = MrCrayfishDeviceMod.proxy.registerApplication(new AppInfo(identifier, Application.class.isAssignableFrom(clazz)), clazz);
		if(application != null)
		{
			APP_INFO.put(identifier, application.getInfo());
			return application;
		}
		return null;
	}


	@Nullable
	public static Application registerApplication(BasicContainer app){
		Application application = MrCrayfishDeviceMod.proxy.registerApplication(app);
		if(application != null){
			APP_INFO.put(app.getId(), application.getInfo());
			return application;
		}
		return null;
	}

	/**
	 * Get all applications that are registered. The returned collection does not include system
	 * applications, see {@link #getSystemApplications()} or {@link #getAllApplications()}. Please
	 * note that this list is read only and cannot be modified.
	 *
	 * @return the application list
	 */
	public static List<AppInfo> getAvailableApplications()
	{
		final Predicate<AppInfo> FILTER = info -> !info.isSystemApp() && (!MrCrayfishDeviceMod.proxy.hasAllowedApplications() || MrCrayfishDeviceMod.proxy.getAllowedApplications().contains(info));
		return APP_INFO.values().stream().filter(FILTER).collect(Collectors.toList());
	}

	public static List<AppInfo> getSystemApplications()
	{
		return APP_INFO.values().stream().filter(AppInfo::isSystemApp).collect(Collectors.toList());
	}

	public static List<AppInfo> getAllApplications()
	{
		return new ArrayList<>(APP_INFO.values());
	}

	@Nullable
	public static AppInfo getApplication(String appId)
	{
		return APP_INFO.get(new ResourceLocation(appId.replace(".", ":")));
	}
}
