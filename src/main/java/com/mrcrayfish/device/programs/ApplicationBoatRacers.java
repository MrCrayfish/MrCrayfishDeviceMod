package com.mrcrayfish.device.programs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.ApplicationBar;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Laptop;
import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.Player;
import com.mrcrayfish.device.object.tiles.Tile;
import com.mrcrayfish.device.util.GuiHelper;
import com.mrcrayfish.device.util.Vec2d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ApplicationBoatRacers extends Application 
{
	private Game game;

	public ApplicationBoatRacers() 
	{
		super("boat_racer", "Boat Racers", ApplicationBar.APP_BAR_GUI, 42, 46);
		this.setDefaultWidth(320);
		this.setDefaultHeight(160);
	}
	
	@Override
	protected void init(int x, int y) 
	{
		super.init(x, y);
		
		try 
		{
			this.game = new Game(x, y, 0, 0, 320, 160);
			super.addComponent(this.game);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		// TODO Auto-generated method stub

	}

	
}
