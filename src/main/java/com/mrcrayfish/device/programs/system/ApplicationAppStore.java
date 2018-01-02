package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.system.layout.LayoutAppPage;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ApplicationAppStore extends SystemApplication
{
	private Layout layoutMain;

	@Override
	public void init()
	{
		layoutMain = new Layout(250, 150);
		layoutMain.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			Gui.drawRect(x, y, x + width, y + 20, Laptop.getSystem().getSettings().getColourScheme().getBackgroundColour());
			Gui.drawRect(x, y + 20, x + width, y + 21, Color.DARK_GRAY.getRGB());
        });

		Button button = new Button(5, 5, "Henlo");
		button.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				Layout layout = new LayoutAppPage(getLaptop(), ApplicationManager.getApplication("cdm:note_stash"));
				this.setCurrentLayout(layout);
				Button btnPrevious = new Button(2, 2, Icons.ARROW_LEFT);
				btnPrevious.setClickListener((mouseX1, mouseY1, mouseButton1) ->
				{
					this.setCurrentLayout(layoutMain);
				});
				layout.addComponent(btnPrevious);
			}
        });
		layoutMain.addComponent(button);

		this.setCurrentLayout(layoutMain);

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
