package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.listener.ChangeListener;
import com.mrcrayfish.device.api.app.renderer.ItemRenderer;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Settings;
import com.mrcrayfish.device.programs.system.component.Palette;
import com.mrcrayfish.device.programs.system.object.ColourScheme;
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
	private Button buttonColourSchemeApply;

	private boolean valueChanged;
	
	public ApplicationSettings() 
	{
		this.setDefaultWidth(100);
		this.setDefaultHeight(40);
	}

	@Override
	public void init() 
	{
		valueChanged = false;

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

		ComboBox.Custom<Integer> colourPicker = new ComboBox.Custom<>(5, 26, 50, 100, 100);
		colourPicker.setValue(Color.RED.getRGB());
		colourPicker.setItemRenderer(new ItemRenderer<Integer>()
		{
			@Override
			public void render(Integer integer, Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				if(integer != null)
				{
					Gui.drawRect(x, y, x + width, y + height, integer);
				}
			}
		});
		colourPicker.setChangeListener((oldValue, newValue) ->
		{
			buttonColourSchemeApply.setEnabled(true);
        });

		Layout layout = colourPicker.getLayout();

		Palette palette = new Palette(5, 5, colourPicker);
		layout.addComponent(palette);

		layoutColourScheme.addComponent(colourPicker);

		buttonColourSchemeApply = new Button(5, 79, Icons.CHECK);
		buttonColourSchemeApply.setEnabled(false);
		buttonColourSchemeApply.setToolTip("Apply", "Set these colours as the new colour scheme");
		buttonColourSchemeApply.setClickListener((c, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				ColourScheme colourScheme = Laptop.getSystem().getSettings().getColourScheme();
				colourScheme.setBackgroundColour(colourPicker.getValue());
				buttonColourSchemeApply.setEnabled(false);
			}
        });
		layoutColourScheme.addComponent(buttonColourSchemeApply);

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
