package com.mrcrayfish.device.object;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class AppInfo 
{
	private final ResourceLocation APP_ID;
	private int iconU = 0, iconV = 0;

	public AppInfo(ResourceLocation appId)
	{
		this.APP_ID = appId;
	}

	/**
	 * Gets the id of the application
	 *
	 * @return the application id
	 */
	public ResourceLocation getId()
	{
		return APP_ID;
	}

	/**
	 * Gets the name of the application
	 *
	 * @return the application name
	 */
	public String getName() 
	{
		return I18n.format("app." + APP_ID.getResourcePath() + ".name");
	}
	
	public String getAuthor() 
	{
		return I18n.format("app." + APP_ID.getResourcePath() + ".author");
	}
	
	public String getDescription() 
	{
		return I18n.format("app." + APP_ID.getResourcePath() + ".desc");
	}

	public int getIconU()
	{
		return iconU;
	}

	public int getIconV()
	{
		return iconV;
	}

	/*public void renderIcon(Minecraft mc, int x, int y)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(icon.getResource());
		RenderUtil.drawRectWithTexture(x, y, icon.getU(), icon.getV(), 14, 14, 14, 14);
	}*/
}
