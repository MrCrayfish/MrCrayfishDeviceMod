package com.mrcrayfish.device.api;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.annotation.DeviceApplication;
import com.mrcrayfish.device.programs.system.SystemApplication;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class ApplicationManager
{
	private static final Map<ResourceLocation, AppInfo> APP_INFO_MAP = new HashMap<>();
	private static List<AppInfo> whitelistedApps;

	private ApplicationManager() {}

	public static void init(ASMDataTable table)
	{
		Set<ASMDataTable.ASMData> apps = table.getAll(DeviceApplication.class.getCanonicalName());
		apps.forEach(asmData ->
		{
			try
			{
				Class clazz = Class.forName(asmData.getClassName());
				if(Application.class.isAssignableFrom(clazz))
				{
					DeviceApplication deviceApp = (DeviceApplication) clazz.getDeclaredAnnotation(DeviceApplication.class);
					ResourceLocation identifier = new ResourceLocation(deviceApp.modId(), deviceApp.appId());

					if(!APP_INFO_MAP.containsKey(identifier))
					{
						AppInfo info = new AppInfo(identifier, clazz, SystemApplication.class.isAssignableFrom(clazz));
						APP_INFO_MAP.put(identifier, info);
					}
					else
					{
						throw new RuntimeException("");
					}
				}
				else
				{
					throw new ClassCastException("The class " + clazz.getCanonicalName() + " is annotated with DeviceApplication but does not extend Application!");
				}
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		});
	}

	@Nullable
	public static Application createApplication(AppInfo info)
	{
		try
		{
			return info.getAppClass().newInstance();
		}
		catch(InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
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
		return APP_INFO_MAP.values().stream().filter(info -> !info.isSystemApp()).collect(Collectors.toList());
	}

	public static List<AppInfo> getSystemApplications()
	{
		return APP_INFO_MAP.values().stream().filter(AppInfo::isSystemApp).collect(Collectors.toList());
	}

	public static List<AppInfo> getAllApplications()
	{
		return new ArrayList<>(APP_INFO_MAP.values());
	}

	@Nullable
	public static AppInfo getApplication(String appId)
	{
		return APP_INFO_MAP.get(new ResourceLocation(appId.replace(".", ":")));
	}

	public static boolean isApplicationWhitelisted(AppInfo info)
	{
		return info.isSystemApp() || whitelistedApps == null || whitelistedApps.contains(info);
	}
}
