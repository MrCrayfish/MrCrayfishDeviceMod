package com.mrcrayfish.device.programs.system;


import java.awt.*;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.utils.RenderUtil;

import com.mrcrayfish.device.programs.system.component.FileList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;

public class ApplicationFileBrowser extends Application
{
	private static final ResourceLocation ASSETS = new ResourceLocation("cdm:textures/gui/file_browser.png");

	private static final int CLICK_INTERVAL = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");

	private Layout main;
	private Button btnPreviousFolder;
	private Button btnNewFolder;
	private Button btnCopy;
	private Button btnPaste;
	private Button btnDelete;
	private FileList fileList;
	private Label label;

	private long lastClick = 0;
	
	public ApplicationFileBrowser() 
	{
		super("file_browser", "File Browser");
		this.setDefaultWidth(240);
		this.setDefaultHeight(151);
	}
	
	@Override
	public void init() 
	{
		super.init();

		main = new Layout(225, 150);
		main.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
			Gui.drawRect(x, y, x + width, y + 20, Color.GRAY.getRGB());
			Gui.drawRect(x, y + 20, x + width, y + 21, Color.DARK_GRAY.getRGB());
        });

		btnPreviousFolder = new Button(5, 2, ASSETS, 40, 20, 10, 10);
		btnPreviousFolder.setEnabled(false);
		btnPreviousFolder.setClickListener((c, mouseButton) -> {
			if(mouseButton == 0) {
				fileList.goToPreviousFolder();
				if(fileList.isRootFolder()) {
					btnPreviousFolder.setEnabled(false);
				}
				updatePath();
			}
		});
		main.addComponent(btnPreviousFolder);

		btnNewFolder = new Button(5, 25, ASSETS, 0, 20, 10, 10);
		btnNewFolder.setClickListener((b, mouseButton) -> {
			Dialog.Input dialog = new Dialog.Input("Enter a name");
			dialog.setResponseListener((success, v) -> {
				if(success) {
					fileList.addFile(new Folder(v));
				}
			});
			dialog.setTitle("Create a Folder");
			dialog.setPositiveText("Create");
			ApplicationFileBrowser.this.openDialog(dialog);
		});
		main.addComponent(btnNewFolder);

		btnCopy = new Button(5, 46, ASSETS, 10, 20, 10, 10);
		btnCopy.setClickListener((b, mouseButton) -> {
			fileList.copyFile();
		});
		main.addComponent(btnCopy);

		btnPaste = new Button(5, 67, ASSETS, 20, 20, 10, 10);
		btnPaste.setClickListener((b, mouseButton) -> {
			fileList.pasteFile();
		});
		main.addComponent(btnPaste);

		btnDelete = new Button(5, 88, ASSETS, 30, 20, 10, 10);
		btnDelete.setClickListener((b, mouseButton) -> {
			File file = fileList.getSelectedItem();
			if(file != null) {
				Dialog.Confirmation dialog = new Dialog.Confirmation();
				StringBuilder builder = new StringBuilder();
				builder.append("Are you sure you want to delete this ");
				if(file instanceof Folder) {
					builder.append("folder");
				} else {
					builder.append("file");
				}
				builder.append(" '");
				builder.append(file.getName());
				builder.append("'?");
				dialog.setMessageText(builder.toString());
				dialog.setTitle("Delete");
				dialog.setPositiveButton("Yes", (c, mouseButton1) -> {
					fileList.removeFile(fileList.getSelectedIndex());
				});
			}
		});
		main.addComponent(btnDelete);

		Folder folder = getFileSystem().getBaseFolder();
		fileList = new FileList(26, 25, 180, 6, folder);
		fileList.setItemClickListener((file, index, mouseButton) -> {
			if(mouseButton == 0) {
				if(System.currentTimeMillis() - this.lastClick <= CLICK_INTERVAL) {
					if(file instanceof Folder) {
						fileList.setSelectedIndex(-1);
						fileList.openFolder((Folder) file, true);
						btnPreviousFolder.setEnabled(true);
						updatePath();
					}
				} else {
					this.lastClick = System.currentTimeMillis();
				}
			}
		});
		main.addComponent(fileList);

		label = new Label("/", 26, 6);
		main.addComponent(label);

		fileList.addFile(new File("shopping list.text", this, new NBTTagCompound()));
		fileList.addFile(new File("track.map", this, new NBTTagCompound()));
		fileList.addFile(new Folder("Pics of Cheese"));

		this.setCurrentLayout(main);
	}

	public void updatePath()
	{
		String path = fileList.getPath();
		int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(path);
		if(width > 190)
		{
			path = "..." + Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(path, 190, true);
		}
		label.setText(path);
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
