package com.mrcrayfish.device.gui;

import com.mrcrayfish.device.app.Window;

import net.minecraft.client.gui.GuiScreen;

public class GuiLaptop extends GuiScreen 
{
	public static final int ID = 0;
	
	private Window window;
	
	public GuiLaptop()
	{
		this.window = new Window(0, 0, 100, 100);
		System.out.println("Opening");
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		this.drawDefaultBackground();
		this.drawCenteredString(fontRendererObj, "Enter Message", this.width / 2, this.height / 2 - 40, 16777215);
		window.render(this, mc);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
