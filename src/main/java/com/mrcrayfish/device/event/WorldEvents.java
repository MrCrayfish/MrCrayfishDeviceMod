package com.mrcrayfish.device.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEvents {
	
	public static boolean displayed = false;
	
	@SubscribeEvent
	public void load(EntityJoinWorldEvent event)
	{
		if(!displayed && event.getEntity() instanceof EntityPlayer && event.getWorld().isRemote) 
		{
			Style style = new Style();
			style.setClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/mrcrayfish"));
			style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.AQUA + "Open MrCrayfish's Patreon")));
			event.getEntity().sendMessage(new TextComponentString(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "MrCrayfish's Device Mod:"));
			event.getEntity().sendMessage(new TextComponentString("You are using a development version of the Device Mod."));
			event.getEntity().sendMessage(new TextComponentString("Please be aware that not all features are finished"));
			event.getEntity().sendMessage(new TextComponentString("and may be completely changed in a future update!"));
			event.getEntity().sendMessage(new TextComponentString(TextFormatting.GOLD.toString() + TextFormatting.BOLD.toString() + "> Support MrCrayfish On Patreon <").setStyle(style));
		}
	}
	
}
