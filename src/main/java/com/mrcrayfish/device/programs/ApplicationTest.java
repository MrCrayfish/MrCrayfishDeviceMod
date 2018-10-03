package com.mrcrayfish.device.programs;

import javax.annotation.Nullable;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.Button;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Author: MrCrayfish
 */
public class ApplicationTest extends Application
{
	@Override
	public void init(@Nullable NBTTagCompound intent)
	{
		Button button = new Button(5, 5, Icons.PRINTER);
		button.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if (mouseButton == 0)
			{
				Dialog.Confirmation dialog = new Dialog.Confirmation("Test");
				dialog.setPositiveText("Override");
				openDialog(dialog);
			}
		});
		super.addComponent(button);

		Button button1 = new Button(30, 5, Icons.PRINTER);
		button1.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if (mouseButton == 0)
			{
				openDialog(new Dialog.Message("est ultricies integer quis auctor elit sed vulputate mi sit amet mauris commodo quis imperdiet massa tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada proin libero nunc consequat interdum varius sit amet"));
			}
		});
		super.addComponent(button1);

		Button button2 = new Button(55, 5, Icons.PRINTER);
		button2.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if (mouseButton == 0)
			{
				openDialog(new Dialog.Input("Test"));
			}
		});
		super.addComponent(button2);

		Button button3 = new Button(80, 5, Icons.PRINTER);
		button3.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if (mouseButton == 0)
			{
				openDialog(new Dialog.OpenFile(this));
			}
		});
		super.addComponent(button3);

		Button button4 = new Button(105, 5, Icons.PRINTER);
		button4.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if (mouseButton == 0)
			{
				openDialog(new Dialog.SaveFile(this, new NBTTagCompound()));
			}
		});
		super.addComponent(button4);
		
		this.setResizable(true);
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
