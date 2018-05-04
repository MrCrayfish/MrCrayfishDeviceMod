package com.mrcrayfish.device.object;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.proxy.ClientProxy;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppInfo 
{
	private transient final ResourceLocation APP_ID;
	private transient int iconU = 0;
	private transient int iconV = 0;
	private transient boolean systemApp;

	private String name;
	private String author;
	private String[] authors;
	private String[] contributors;
	private String description;
	private String version;
	private String icon;
	private String[] screenshots;
	private Support support;
	private String[] bundle;

	public AppInfo(ResourceLocation identifier, boolean isSystemApp)
	{
		this.APP_ID = identifier;
		this.systemApp = isSystemApp;
	}

	/**
	 * Gets the id of the application
	 *
	 * @return the app resource location
	 */
	public ResourceLocation getId()
	{
		return APP_ID;
	}

	/**
	 * Gets the formatted version of the application's id
	 *
	 * @return a formatted id
	 */
	public String getFormattedId()
	{
		return APP_ID.getResourceDomain() + "." + APP_ID.getResourcePath();
	}

	/**
	 * Gets the name of the application
	 *
	 * @return the application name
	 */
	public String getName()
	{
		return name;
	}
	
	public String getAuthor() 
	{
		return author;
	}

	public boolean hasSingleAuthor(){
		return (this.author != null && this.authors == null);
	}

	public String[] getAuthors(){ return authors; }

	public String[] getContributors(){ return contributors; }
	
	public String getDescription() 
	{
		return description;
	}

	public String getVersion()
	{
		return version;
	}

	public String getIcon()
	{
		return icon;
	}

	public int getIconU()
	{
		return iconU;
	}

	public int getIconV()
	{
		return iconV;
	}

	public String[] getScreenshots()
	{
		return screenshots;
	}

	public Support getSupport()
	{
		return support;
	}

	public boolean isSystemApp()
	{
		return systemApp;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		if(!(obj instanceof AppInfo)) return false;
		AppInfo info = (AppInfo) obj;
		return this == info || getFormattedId().equals(info.getFormattedId());
	}

	public void reload()
	{
		resetInfo();
		InputStream stream = ClientProxy.class.getResourceAsStream("/assets/" + APP_ID.getResourceDomain() + "/apps/" + APP_ID.getResourcePath() + ".json");

		if(stream == null)
			throw new RuntimeException("Missing app info json for '" + APP_ID + "'");

		Reader reader = new InputStreamReader(stream);
		JsonParser parser = new JsonParser();
		JsonElement obj = parser.parse(reader);
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(AppInfo.class, new AppInfo.Deserializer(this));
		Gson gson = builder.create();
		gson.fromJson(obj, AppInfo.class);
	}

	private void resetInfo()
	{
		name = null;
		author = null;
		description = null;
		version = null;
		icon = null;
		screenshots = null;
		support = null;
	}

	private static class Support
	{
		private String paypal;
		private String patreon;
		private String twitter;
		private String youtube;
	}

	public static class Deserializer implements JsonDeserializer<AppInfo>
	{
		private static final Pattern LANG = Pattern.compile("\\$\\{[a-z]+}");

		private AppInfo info;

		private static final String PROP_NAME = "name";
		private static final String PROP_AUTHOR = "author";
		private static final String PROP_AUTHORS = "authors";
		private static final String PROP_CONTRIB = "contributors";
		private static final String PROP_DESC = "description";
		private static final String PROP_VERSION = "version";
		private static final String PROP_SCREENSHOTS = "screenshots";
		private static final String PROP_ICON = "icon";
		private static final String PROP_SUPPORT = "support";

		public Deserializer(AppInfo info)
		{
			this.info = info;
		}

		@Override
		public AppInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		{
			try
			{
				info.name = convertToLocal(json.getAsJsonObject().get(PROP_NAME).getAsString());
				if(json.getAsJsonObject().has(PROP_AUTHOR))
					info.author = convertToLocal(json.getAsJsonObject().get(PROP_AUTHOR).getAsString());
				else if(json.getAsJsonObject().has(PROP_AUTHOR) && json.getAsJsonObject().get(PROP_AUTHORS).isJsonArray()){
					info.authors = deserializeArray(context, PROP_AUTHORS, json.getAsJsonObject());
				}
				if(json.getAsJsonObject().has(PROP_CONTRIB) && json.getAsJsonObject().get(PROP_CONTRIB).isJsonArray()){
				    info.contributors = deserializeArray(context, PROP_AUTHORS, json.getAsJsonObject());
                }
				info.description = convertToLocal(json.getAsJsonObject().get(PROP_DESC).getAsString());
				info.version = json.getAsJsonObject().get(PROP_VERSION).getAsString();

				if(json.getAsJsonObject().has(PROP_SCREENSHOTS) && json.getAsJsonObject().get(PROP_SCREENSHOTS).isJsonArray())
				{
					info.screenshots = deserializeArray(context, PROP_SCREENSHOTS, json.getAsJsonObject());
				}

				if(json.getAsJsonObject().has(PROP_ICON) && json.getAsJsonObject().get(PROP_ICON).isJsonPrimitive())
				{
					info.icon = json.getAsJsonObject().get(PROP_ICON).getAsString();
				}

				if(json.getAsJsonObject().has(PROP_SUPPORT) && json.getAsJsonObject().get(PROP_SUPPORT).getAsJsonObject().size() > 0)
				{
					JsonObject supportObj = json.getAsJsonObject().get(PROP_SUPPORT).getAsJsonObject();
					Support support = new Support();

					if(supportObj.has("paypal"))
					{
						support.paypal = supportObj.get("paypal").getAsString();
					}
					if(supportObj.has("patreon"))
					{
						support.patreon = supportObj.get("patreon").getAsString();
					}
					if(supportObj.has("twitter"))
					{
						support.twitter = supportObj.get("twitter").getAsString();
					}
					if(supportObj.has("youtube"))
					{
						support.youtube = supportObj.get("youtube").getAsString();
					}

					info.support = support;
				}
			}
			catch(JsonParseException e)
			{
				MrCrayfishDeviceMod.getLogger().error("Malformed app info json for '" + info.getFormattedId() + "'");
			}

			return info;
		}

		private <T> T[] deserializeArray(JsonDeserializationContext context, String name, JsonObject jsonObject){
			return context.deserialize(jsonObject.getAsJsonObject().get(name), new TypeToken<T>(){}.getType());
		}

		private String convertToLocal(String s)
		{
			Matcher m = LANG.matcher(s);
			while(m.find())
			{
				String found = m.group();
				s = s.replace(found, I18n.format("app." + info.getFormattedId() + "." + found.substring(2, found.length() - 1)));
			}
			return s;
		}
	}
}
