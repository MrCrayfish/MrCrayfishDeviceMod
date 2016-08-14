package com.mrcrayfish.device.object;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.core.TaskBar;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class AppInfo 
{
	private String name;
	private String author = "MrCrayfish";
	private String description = "Hallo";
	
	private ResourceLocation iconResource = TaskBar.APP_BAR_GUI;
	private int iconU = 0, iconV = 46;
	
	public AppInfo(String name) 
	{
		this.name = name;
	}
	
	public String getName() 
	{
		return EnumChatFormatting.YELLOW + name;
	}
	
	public String getAuthor() 
	{
		return author;
	}
	
	public String getDescription() 
	{
		return description;
	}
	
	@Override
	public String toString() 
	{
		return name;
	}
	
	public void renderIcon(Minecraft mc, int x, int y)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(iconResource);
		GuiHelper.drawModalRectWithUV(x, y, iconU, iconV, 14, 14, 14, 14);
	}
}
