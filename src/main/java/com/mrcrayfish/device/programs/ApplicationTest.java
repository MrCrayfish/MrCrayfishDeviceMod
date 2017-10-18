package com.mrcrayfish.device.programs;

import java.awt.Color;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.listener.ClickListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationTest extends Application
{
	public ApplicationTest()
	{
		//super(Reference.MOD_ID + "TestApp", "Test App");
	}
	
	@Override
	public void init()
	{
		Button button = new Button("Open", 5, 5, 50, 20);
		button.setClickListener((c, mouseButton) ->
		{
			Dialog.OpenFile dialog = new Dialog.OpenFile(this);
			dialog.setFilter(file -> file.getOpeningApp().equals("cdm.note_stash"));
			openDialog(dialog);
        });

		this.addComponent(button);
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
