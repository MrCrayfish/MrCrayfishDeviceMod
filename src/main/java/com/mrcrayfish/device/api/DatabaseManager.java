package com.mrcrayfish.device.api;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Database;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class DatabaseManager
{
	public static final DatabaseManager INSTANCE = new DatabaseManager();
	
	private static final String APP_FOLDER = "data/databases";
	private final DatabaseMap DATABASE_MAP;
	private boolean loaded = false;
	
	private final int SAVE_INTERVAL = 24000;
	private int saveCounter = 0;

	private DatabaseManager() 
	{
		DATABASE_MAP = new DatabaseMap();
	}

	public void register(Application application, Database database)
	{
		System.out.println("Registering database '" + database.getName() + "'");
		
		if(DATABASE_MAP.contains(application.getID(), database))
		{
			throw new DatabaseException("A database with the name '" + database.getName() + "' already exists for the application '" + application.getID() + "'");
		}
		if(!DATABASE_MAP.containsKey(application.getID())) 
		{
			DATABASE_MAP.put(application.getID(), new ArrayList<Database>());
		}
		DATABASE_MAP.get(application.getID()).add(database);
	}

	@SubscribeEvent
	public void load(WorldEvent.Load event)
	{
		if(event.getWorld().provider.getDimension() != 0 || loaded) return;
		loadDatabases();
	}
	
	@SubscribeEvent
	public void save(WorldEvent.Save event)
	{
		if(event.getWorld().provider.getDimension() != 0) return;
		saveDatabases();
	}
	
	@SubscribeEvent
	public void tick(ServerTickEvent event) 
	{
		if(event.phase == Phase.END) 
		{
			saveCounter++;
			if(saveCounter >= SAVE_INTERVAL) 
			{
				System.out.println("Saving");
				saveDatabases();
				saveCounter = 0;
			}
		}
	}
	
	private void loadDatabases() 
	{
		File databaseFolder = getDatabaseFolder();
		for(String appId : DATABASE_MAP.keySet())
		{
			List<Database> databases = DATABASE_MAP.get(appId);
			for(Database database : databases)
			{
				readDatabase(databaseFolder, appId, database);
			}
		}
		loaded = true;
	}
	
	private void saveDatabases()
	{
		File databaseFolder = getDatabaseFolder();
		for(String appId : DATABASE_MAP.keySet())
		{
			List<Database> databases = DATABASE_MAP.get(appId);
			for(Database database : databases) 
			{
				writeDatabase(databaseFolder, appId, database);
			}
		}
	}
	
	private void readDatabase(File databaseFolder, String appId, Database database)
	{
		try
		{
			File appFolder = new File(databaseFolder, appId);
			if(appFolder.exists() && appFolder.isDirectory())
			{
				File databaseFile = new File(appFolder, database.getName() + ".dat");
				if(databaseFile.exists() && databaseFile.isFile())
				{
					NBTTagCompound tag = CompressedStreamTools.read(databaseFile);
					database.load(tag);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void writeDatabase(File databaseFolder, String appId, Database database)
	{
		try
		{
			File appFolder = new File(databaseFolder, appId);
			if(!appFolder.exists() || !appFolder.isDirectory())
			{
				appFolder.mkdir();
			}
			
			File databaseFile = new File(appFolder, database.getName() + ".dat");
			if (!databaseFile.exists())
			{
				databaseFile.createNewFile();
			}
			
			NBTTagCompound tag = new NBTTagCompound();
			database.save(tag);
			CompressedStreamTools.write(tag, databaseFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private File getDatabaseFolder()
	{
		File folder = new File(DimensionManager.getCurrentSaveRootDirectory(), APP_FOLDER);
		if(!folder.exists() || !folder.isDirectory())
		{
			folder.mkdir();
		}
		return folder;
	}
	
	private static class DatabaseException extends RuntimeException
	{
		private String message;
		
		public DatabaseException() {}
		
		public DatabaseException(String message) 
		{
			this.message = message;
		}
		
		@Override
		public String getMessage()
		{
			return message;
		}
	}
	
	private static class DatabaseComparator implements Comparator<Database>
	{
		@Override
		public int compare(Database database, Database otherDatabase)
		{
			return database.getName().equals(otherDatabase.getName()) ? 1 : 0;
		}
	}

	private static class DatabaseMap extends HashMap<String, ArrayList<Database>>
	{
		private static final DatabaseComparator COMPARATOR = new DatabaseComparator();
		
		public boolean contains(String appId, Database database)
		{
			if(!containsKey(appId))	return false;
			List<Database> databases = get(appId);
			if(databases == null) return false;
			for(int i = 0; i < databases.size(); i++)
			{
				if(COMPARATOR.compare(database, databases.get(i)) == 1)
				{
					return true;
				}
			}
			return false;
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Instance {
		
        String value() default "";
    }
}
