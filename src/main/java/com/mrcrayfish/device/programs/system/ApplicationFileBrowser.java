package com.mrcrayfish.device.programs.system;


import java.awt.Color;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.utils.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ApplicationFileBrowser extends Application
{
	private static final ResourceLocation ASSETS = new ResourceLocation("cdm:textures/gui/file_browser.png");
	
	private static final Color ITEM_BACKGROUND = new Color(215, 217, 224);
	private static final Color ITEM_SELECTED = new Color(221, 208, 208);
	
	private ItemList fileList;
	
	public ApplicationFileBrowser() 
	{
		super("file_browser", "File Browser");
		this.setDefaultWidth(300);
		this.setDefaultHeight(151);
	}
	
	@Override
	public void init() 
	{
		super.init();
		
		Folder folder = getFileSystem().getBaseFolder();
		folder.add(new File("File", this, null));
		folder.add(new File("Image", this, null));
		
		fileList = new ItemList<File>(5, 5, 240, 7);
		fileList.setListItemRenderer(new ListItemRenderer<File>(19) {
			@Override
			public void render(File file, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected) 
			{
				gui.drawRect(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());	
				
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				Minecraft.getMinecraft().getTextureManager().bindTexture(ASSETS);
				if(file.isFolder())
				{
					RenderUtil.drawRectWithTexture(x + 5, y + 3, 0, 0, 16, 14, 16, 14);
				}
				else
				{
					RenderUtil.drawRectWithTexture(x + 4, y + 2, 16, 0, 16, 16, 16, 16);
					ResourceLocation appIcon = ApplicationManager.getApp(file.getOpeningApp()).getIcon();
					Minecraft.getMinecraft().getTextureManager().bindTexture(appIcon);
					RenderUtil.drawRectWithTexture(x + width - 18, y + 2, 16, 0, 16, 16, 16, 16);
				}
				gui.drawString(Minecraft.getMinecraft().fontRendererObj, file.getName(), x + 26, y + 5, Color.WHITE.getRGB());
			}
		});
		this.addComponent(fileList);
		
		fileList.setItems(folder.getFiles());
	}

	@Override
	public void load(NBTTagCompound tagCompound) 
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) 
	{
		
	}

}
