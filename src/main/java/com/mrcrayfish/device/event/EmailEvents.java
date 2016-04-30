package com.mrcrayfish.device.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.mrcrayfish.device.app.ApplicationEmail.EmailManager;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EmailEvents 
{
	@SubscribeEvent
	public void load(WorldEvent.Load event)
	{
		if(event.world.provider.getDimensionId() == 0)
		{
			try 
			{
				File data = new File(DimensionManager.getCurrentSaveRootDirectory(), "emails.dat");
				if(!data.exists())
				{
					return;
				}
				
				NBTTagCompound nbt = CompressedStreamTools.read(data);
				if(nbt != null)
				{
					EmailManager.readFromNBT(nbt);
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void save(WorldEvent.Save event)
	{
		if(event.world.provider.getDimensionId() == 0)
		{
			try 
			{
				File data = new File(DimensionManager.getCurrentSaveRootDirectory(), "emails.dat");
				if(!data.exists())
				{
					data.createNewFile();
				}
				
				NBTTagCompound nbt = new NBTTagCompound();
				EmailManager.writeToNBT(nbt);
				System.out.println(nbt);
				CompressedStreamTools.write(nbt, data);
				
				System.out.println("Saved data");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
