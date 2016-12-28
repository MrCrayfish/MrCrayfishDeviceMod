package com.mrcrayfish.device.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEvents {
	
	public static boolean displayed = false;
	
	@SubscribeEvent
	public void load(EntityJoinWorldEvent event)
	{
		if(!displayed && event.entity instanceof EntityPlayer && event.world.isRemote) 
		{
			ChatStyle style = new ChatStyle();
			style.setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/mrcrayfish"));
			style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.AQUA + "Open MrCrayfish's Patreon")));
			event.entity.addChatMessage(new ChatComponentText(EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD.toString() + "MrCrayfish's Device Mod:"));
			event.entity.addChatMessage(new ChatComponentText("You are using a development version of the Device Mod."));
			event.entity.addChatMessage(new ChatComponentText("Please be aware that not all features are finished"));
			event.entity.addChatMessage(new ChatComponentText("and may be completely changed in a future update!"));
			event.entity.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.BOLD.toString() + "> Support MrCrayfish On Patreon <").setChatStyle(style));
		}
	}
	
}
