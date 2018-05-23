package com.mrcrayfish.device.proxy;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.registry.BasicContainer;
import com.mrcrayfish.device.api.registry.DeviceModRegistry;
import com.mrcrayfish.device.api.registry.app.ApplicationRegistry;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.registry.task.TaskRegistry;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.task.MessageSyncApplications;
import com.mrcrayfish.device.network.task.MessageSyncConfig;
import com.mrcrayfish.device.object.AppInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mrcrayfish.device.MrCrayfishDeviceMod.asmTable;

public class CommonProxy
{
	List<AppInfo> allowedApps;
	int hashCode = -1;

	public void preInit()
	{
		MinecraftForge.EVENT_BUS.register(this);
		registerRegistryPlugins();
	}

	private void registerRegistryPlugins(){
		DeviceModRegistry.addCDMRegistry(ApplicationRegistry.class);
		DeviceModRegistry.addCDMRegistry(TaskRegistry.class);
	}

	public void init() {
		DeviceModRegistry.startRegistries(asmTable);
	}

	public void postInit() {}

	public Application registerApplication(BasicContainer app){
		if(allowedApps == null){
			allowedApps = new ArrayList<>();
		}
		allowedApps.add(new AppInfo(app.getId(), app.isSystem()));
		return null;
	}

	@Nullable
	public Application registerApplication(AppInfo info, Class<? extends Application> clazz)
	{
		if(allowedApps == null)
		{
			allowedApps = new ArrayList<>();
		}
		allowedApps.add(info);
		return null;
	}

	public boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint)
	{
		return true;
	}

	public boolean hasAllowedApplications()
	{
		return allowedApps != null;
	}

	public List<AppInfo> getAllowedApplications()
	{
		if(allowedApps == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(allowedApps);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if(allowedApps != null)
		{
			PacketHandler.INSTANCE.sendTo(new MessageSyncApplications(allowedApps), (EntityPlayerMP) event.player);
		}
		PacketHandler.INSTANCE.sendTo(new MessageSyncConfig(), (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		World world = event.getWorld();
		if(!event.getItemStack().isEmpty() && event.getItemStack().getItem() == Items.PAPER)
		{
			if(world.getBlockState(event.getPos()).getBlock() == DeviceBlocks.PRINTER)
			{
				event.setUseBlock(Event.Result.ALLOW);
			}
		}
	}

	public void showNotification(NBTTagCompound tag) {}
}
