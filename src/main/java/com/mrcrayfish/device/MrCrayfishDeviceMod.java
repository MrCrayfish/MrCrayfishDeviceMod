package com.mrcrayfish.device;

import org.apache.logging.log4j.Logger;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.io.task.TaskGetFiles;
import com.mrcrayfish.device.core.io.task.TaskGetMainDrive;
import com.mrcrayfish.device.core.io.task.TaskGetStructure;
import com.mrcrayfish.device.core.io.task.TaskSendAction;
import com.mrcrayfish.device.core.io.task.TaskSetupFileBrowser;
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
import com.mrcrayfish.device.programs.ApplicationIcons;
import com.mrcrayfish.device.programs.ApplicationNoteStash;
import com.mrcrayfish.device.programs.ApplicationPixelPainter;
import com.mrcrayfish.device.programs.ApplicationTest;
import com.mrcrayfish.device.programs.debug.ApplicationTextArea;
import com.mrcrayfish.device.programs.email.ApplicationEmail;
import com.mrcrayfish.device.programs.email.task.TaskCheckEmailAccount;
import com.mrcrayfish.device.programs.email.task.TaskDeleteEmail;
import com.mrcrayfish.device.programs.email.task.TaskRegisterEmailAccount;
import com.mrcrayfish.device.programs.email.task.TaskSendEmail;
import com.mrcrayfish.device.programs.email.task.TaskUpdateInbox;
import com.mrcrayfish.device.programs.email.task.TaskViewEmail;
import com.mrcrayfish.device.programs.example.ApplicationExample;
import com.mrcrayfish.device.programs.example.task.TaskNotificationTest;
import com.mrcrayfish.device.programs.gitweb.ApplicationGitWeb;
import com.mrcrayfish.device.programs.system.ApplicationAppStore;
import com.mrcrayfish.device.programs.system.ApplicationBank;
import com.mrcrayfish.device.programs.system.ApplicationFileBrowser;
import com.mrcrayfish.device.programs.system.ApplicationSettings;
import com.mrcrayfish.device.programs.system.task.TaskAdd;
import com.mrcrayfish.device.programs.system.task.TaskDeposit;
import com.mrcrayfish.device.programs.system.task.TaskGetBalance;
import com.mrcrayfish.device.programs.system.task.TaskPay;
import com.mrcrayfish.device.programs.system.task.TaskRemove;
import com.mrcrayfish.device.programs.system.task.TaskUpdateApplicationData;
import com.mrcrayfish.device.programs.system.task.TaskUpdateSystemData;
import com.mrcrayfish.device.programs.system.task.TaskWithdraw;
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

/**
 * The main mod class for {@link MrCrayfishDeviceMod}.
 * 
 * <br>
 * </br>
 * 
 * <b>Author: MrCrayfish</b>
 */
@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.WORKING_MC_VERSION)
public class MrCrayfishDeviceMod
{
	/** The instance of this mod. Used for GUIS and such */
	@Instance(Reference.MOD_ID)
	public static MrCrayfishDeviceMod instance;

	/** The proxies used to separate the client and server */
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;

	/** The creative tab that contains the items/blocks in the mod */
	public static final CreativeTabs TAB_DEVICE = new DeviceTab(Reference.MOD_ID + "TabDevice");

	/** Whether or not the device mod has extra features enabled */
	public static final boolean DEVELOPER_MODE = true;

	private static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws LaunchException
	{
		if (DEVELOPER_MODE && !(Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"))
		{
			throw new LaunchException();
		}
		logger = event.getModLog();

		DeviceConfig.load(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new DeviceConfig());

		RegistrationHandler.init();

		proxy.preInit();
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

	/**
	 * Registers all the apps and tasks to do with the core device mod.
	 */
	private void registerApplications()
	{
		// Applications (Both)
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "settings"), ApplicationSettings.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "bank"), ApplicationBank.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "file_browser"), ApplicationFileBrowser.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "gitweb"), ApplicationGitWeb.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "note_stash"), ApplicationNoteStash.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "pixel_painter"), ApplicationPixelPainter.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "ender_mail"), ApplicationEmail.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "app_store"), ApplicationAppStore.class);

		// Core
		TaskManager.registerTask(TaskInstallApp.class);
		TaskManager.registerTask(TaskUpdateApplicationData.class);
		TaskManager.registerTask(TaskPrint.class);
		TaskManager.registerTask(TaskUpdateSystemData.class);
		TaskManager.registerTask(TaskConnect.class);
		TaskManager.registerTask(TaskPing.class);
		TaskManager.registerTask(TaskGetDevices.class);

		// Bank
		TaskManager.registerTask(TaskDeposit.class);
		TaskManager.registerTask(TaskWithdraw.class);
		TaskManager.registerTask(TaskGetBalance.class);
		TaskManager.registerTask(TaskPay.class);
		TaskManager.registerTask(TaskAdd.class);
		TaskManager.registerTask(TaskRemove.class);

		// File browser
		TaskManager.registerTask(TaskSendAction.class);
		TaskManager.registerTask(TaskSetupFileBrowser.class);
		TaskManager.registerTask(TaskGetFiles.class);
		TaskManager.registerTask(TaskGetStructure.class);
		TaskManager.registerTask(TaskGetMainDrive.class);

		// Ender Mail
		TaskManager.registerTask(TaskUpdateInbox.class);
		TaskManager.registerTask(TaskSendEmail.class);
		TaskManager.registerTask(TaskCheckEmailAccount.class);
		TaskManager.registerTask(TaskRegisterEmailAccount.class);
		TaskManager.registerTask(TaskDeleteEmail.class);
		TaskManager.registerTask(TaskViewEmail.class);

		if (!DEVELOPER_MODE)
		{
			// Applications (Normal)
			// ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "boat_racers"), ApplicationBoatRacers.class);
			// ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "mine_bay"), ApplicationMineBay.class);

			// Tasks (Normal)
			// TaskManager.registerTask(TaskAddAuction.class);
			// TaskManager.registerTask(TaskGetAuctions.class);
			// TaskManager.registerTask(TaskBuyItem.class);
		} else
		{
			// Applications (Developers)
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "example"), ApplicationExample.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "icons"), ApplicationIcons.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "text_area"), ApplicationTextArea.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "test"), ApplicationTest.class);

			TaskManager.registerTask(TaskNotificationTest.class);
		}

		PrintingManager.registerPrint(new ResourceLocation(Reference.MOD_ID, "picture"), ApplicationPixelPainter.PicturePrint.class);
	}

	/**
	 * @return {@link MrCrayfishDeviceMod}'s logger instance
	 */
	public static Logger logger()
	{
		return logger;
	}
}
