package com.mrcrayfish.device;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.io.task.*;
import com.mrcrayfish.device.core.network.task.TaskConnect;
import com.mrcrayfish.device.core.network.task.TaskGetDevices;
import com.mrcrayfish.device.core.network.task.TaskPing;
import com.mrcrayfish.device.core.print.task.TaskPrint;
import com.mrcrayfish.device.core.task.TaskInstallApp;
import com.mrcrayfish.device.entity.EntitySeat;
import com.mrcrayfish.device.event.BankEvents;
import com.mrcrayfish.device.event.EmailEvents;
import com.mrcrayfish.device.gui.GuiHandler;
import com.mrcrayfish.device.init.DeviceTileEntites;
import com.mrcrayfish.device.init.RegistrationHandler;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.programs.ApplicationPixelPainter;
import com.mrcrayfish.device.programs.email.task.*;
import com.mrcrayfish.device.programs.example.task.TaskNotificationTest;
import com.mrcrayfish.device.programs.system.task.*;
import com.mrcrayfish.device.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.WORKING_MC_VERSION)
public class MrCrayfishDeviceMod 
{
	@Instance(Reference.MOD_ID)
	public static MrCrayfishDeviceMod instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
	
	public static final CreativeTabs TAB_DEVICE = new DeviceTab("cdmTabDevice");

	private static Logger logger;

	public static final boolean DEVELOPER_MODE = true;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws LaunchException
	{
		if(DEVELOPER_MODE && !(Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"))
		{
			throw new LaunchException();
		}
		logger = event.getModLog();

		proxy.preInit();

		DeviceConfig.load(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new DeviceConfig());

		RegistrationHandler.init();
		ApplicationManager.init(event.getAsmData());
		TaskManager.init(event.getAsmData());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) 
	{
		/* Tile Entity Registering */
		DeviceTileEntites.register();

		EntityRegistry.registerModEntity(new ResourceLocation("cdm:seat"), EntitySeat.class, "Seat", 0, this, 80, 1, false);

		/* Packet Registering */
		PacketHandler.init();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		MinecraftForge.EVENT_BUS.register(new EmailEvents());
		MinecraftForge.EVENT_BUS.register(new BankEvents());

		registerApplications();

		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) 
	{
		proxy.postInit();
	}

	private void registerApplications()
	{
		PrintingManager.registerPrint(new ResourceLocation(Reference.MOD_ID, "picture"), ApplicationPixelPainter.PicturePrint.class);
	}

	public static Logger getLogger()
	{
		return logger;
	}
}
