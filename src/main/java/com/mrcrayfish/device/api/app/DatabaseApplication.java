package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.api.DatabaseManager;
import com.mrcrayfish.device.api.DatabaseManager.IDatabase;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class DatabaseApplication extends Application
{
	public static final Database database = new Database();
	
	public DatabaseApplication(String appId, String displayName)
	{
		this(appId, displayName, null, 0, 0);
	}
	
	public DatabaseApplication(String appId, String displayName, ResourceLocation icon, int iconU, int iconV)
	{
		super(appId, displayName, icon, iconU, iconV);
		if(FMLCommonHandler.instance().getSide() == Side.SERVER)
		{
			DatabaseManager.INSTANCE.register(this, Database.class);
		}
	}
	
	private static class Database implements IDatabase
	{
		private NBTTagCompound tag;
		
		public Database()
		{
			tag = new NBTTagCompound();
		}
		
		@Override
		public String getName()
		{
			return "default";
		}

		@Override
		public void save(NBTTagCompound tag)
		{
			
		}

		@Override
		public void load(NBTTagCompound tag)
		{
			
		}
		
		public void setByte(String key, byte val)
		{
			
		}
		
		public void setShort(String key, byte val)
		{
			
		}
		
		public void setInt(String key, byte val)
		{
			
		}
		
		public void setLong(String key, byte val)
		{
			
		}
		
		public void setFloat(String key, byte val)
		{
			
		}
		
		public void setDouble(String key, byte val)
		{
			
		}
		
		public void setBool(String key, byte val)
		{
			
		}
		
		public void setChar(String key, byte val)
		{
			
		}
		
		public void setTag(String key, NBTBase nbt)
		{
			
		}
	}
	
	private class DatabaseImpl implements IDatabase
	{

		@Override
		public String getName()
		{
			return "default";
		}

		@Override
		public void save(NBTTagCompound tag)
		{
			
		}

		@Override
		public void load(NBTTagCompound tag)
		{
			
		}
		
	}
}
