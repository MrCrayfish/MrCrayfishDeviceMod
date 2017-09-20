package com.mrcrayfish.device.object;

import com.mrcrayfish.device.api.app.Application;
import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.TaskBar;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class AppInfo 
{

	private String name;
	private String author = "MrCrayfish";
	private String description = "Hallo";
	private Application.Icon icon;

	public AppInfo(String name)
	{
		this.name = name;
	}
	
	public String getName() 
	{
		return TextFormatting.YELLOW + name;
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
		mc.getTextureManager().bindTexture(icon.getResource());
		RenderUtil.drawRectWithTexture(x, y, icon.getU(), icon.getV(), 14, 14, 14, 14);
	}
}
