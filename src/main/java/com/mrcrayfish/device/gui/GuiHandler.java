package com.mrcrayfish.device.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		System.out.println("Calleds");
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		System.out.println("Calledc");
		if(ID == GuiLaptop.ID)
		{
			System.out.println("Opening on client");
			return new GuiLaptop();
		}
		return null;
	}
}
