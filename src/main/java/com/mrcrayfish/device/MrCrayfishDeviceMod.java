package com.mrcrayfish.device;

import com.mrcrayfish.device.app.ApplicationAppStore;
import com.mrcrayfish.device.app.ApplicationBar;
import com.mrcrayfish.device.app.ApplicationEmail;
import com.mrcrayfish.device.app.ApplicationExample;
import com.mrcrayfish.device.app.ApplicationNoteStash;
import com.mrcrayfish.device.app.ApplicationPixelPainter;
import com.mrcrayfish.device.app.Laptop;
import com.mrcrayfish.device.app.requests.TaskCheckEmailAccount;
import com.mrcrayfish.device.app.requests.TaskRegisterEmailAccount;
import com.mrcrayfish.device.app.requests.TaskSendEmail;
import com.mrcrayfish.device.app.requests.TaskUpdateInbox;
import com.mrcrayfish.device.event.EmailEvents;
import com.mrcrayfish.device.gui.GuiHandler;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.init.DeviceCrafting;
import com.mrcrayfish.device.init.DeviceTileEntites;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.proxy.IProxyInterface;
import com.mrcrayfish.device.task.TaskManager;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.WORKING_MC_VERSION)
public class MrCrayfishDeviceMod 
{
	@Instance(Reference.MOD_ID)
	public static MrCrayfishDeviceMod instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static IProxyInterface proxy;
	
	public static CreativeTabs tabDevice = new DeviceTab("cdmTabDevice");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) 
	{
		/** Block Registering */
		DeviceBlocks.init();
		DeviceBlocks.register();
		
		/** Packet Registering */
		PacketHandler.init();
		
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) 
	{
		/** Crafting Registering */
		DeviceCrafting.register();
		
		/** Tile Entity Registering */
		DeviceTileEntites.register();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		MinecraftForge.EVENT_BUS.register(new EmailEvents());
		
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) 
	{
		ApplicationBar.registerApplication(new ApplicationNoteStash());
		ApplicationBar.registerApplication(new ApplicationAppStore());
		ApplicationBar.registerApplication(new ApplicationExample());
		ApplicationBar.registerApplication(new ApplicationPixelPainter());
		ApplicationBar.registerApplication(new ApplicationEmail());
		
		Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_1.png"));
		Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_2.png"));
		Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_3.png"));
		Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_4.png"));
		Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_5.png"));
		Laptop.addWallpaper(new ResourceLocation("cdm:textures/gui/laptop_wallpaper_6.png"));
		
		TaskManager.registerRequest(TaskUpdateInbox.class);
		TaskManager.registerRequest(TaskSendEmail.class);
		TaskManager.registerRequest(TaskCheckEmailAccount.class);
		TaskManager.registerRequest(TaskRegisterEmailAccount.class);
		
		proxy.postInit();
	}
	
}