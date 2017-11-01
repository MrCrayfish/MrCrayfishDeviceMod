package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

import java.awt.*;

public class ApplicationSettings extends SystemApplication
{
	private Layout layoutMain;
	private Layout layoutGeneral;
	private CheckBox checkBoxShowApps;
	private Button btnWallpaperNext;
	private Button btnWallpaperPrev;

	private Layout layoutColourScheme;
	
	public ApplicationSettings() 
	{
		this.setDefaultWidth(100);
		this.setDefaultHeight(40);
	}

	@Override
	public void init() 
	{
		layoutMain = new Layout(100, 50);
		layoutMain.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			Gui.drawRect(x, y, x + width, y + 20, Laptop.getSystem().getSettings().getColourScheme().getBackgroundColour());
			Gui.drawRect(x, y + 20, x + width, y + 21, Color.DARK_GRAY.getRGB());
		});

		Button buttonColourScheme = new Button(5, 26, "Appearance", Icons.EDIT);
		buttonColourScheme.setToolTip("Appearance", "Change the system colour scheme");
		buttonColourScheme.setClickListener((c, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				setCurrentLayout(layoutColourScheme);
			}
        });
		layoutMain.addComponent(buttonColourScheme);

		layoutGeneral = new Layout(100, 50);

		checkBoxShowApps = new CheckBox("Show All Apps", 5, 5);
		checkBoxShowApps.setSelected(Settings.isShowAllApps());
		checkBoxShowApps.setClickListener((c, mouseButton) ->
		{
			Settings.setShowAllApps(checkBoxShowApps.isSelected());
			Laptop laptop = getLaptop();
			laptop.getTaskBar().setupApplications(laptop.getApplications());
        });
		layoutGeneral.addComponent(checkBoxShowApps);

		layoutColourScheme = new Layout(100, 100);
		layoutColourScheme.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			Gui.drawRect(x, y, x + width, y + 20, Laptop.getSystem().getSettings().getColourScheme().getBackgroundColour());
			Gui.drawRect(x, y + 20, x + width, y + 21, Color.DARK_GRAY.getRGB());
        });

		Button button = new Button(5, 26, Icons.EDIT);
		button.setClickListener((c, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				Dialog.Input dialog = new Dialog.Input();
				dialog.setResponseHandler((success, s) ->
				{
					if(success)
					{
						try
						{
							Color color = Color.decode(s);
							Laptop.getSystem().getSettings().getColourScheme().setBackgroundColour(color.getRGB());
						}
						catch(NumberFormatException e)
						{
							Dialog.Message dialog1 = new Dialog.Message("Invalid colour!");
							openDialog(dialog1);
							return false;
						}
					}
					return success;
				});
				openDialog(dialog);
			}
        });
		layoutColourScheme.addComponent(button);

		setCurrentLayout(layoutMain);
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
