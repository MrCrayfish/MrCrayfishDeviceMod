package com.mrcrayfish.device.object;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.proxy.ClientProxy;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

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
	private String description;
	private String version;
	private String icon;
	private String[] screenshots;
	private Support support;
	private String[] contributors;

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

	public boolean hasContributors(){
		return this.contributors != null && this.contributors.length > 0;
	}

	public String[] getContributors(){
		return this.contributors;
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

		private static final String NAME = "name";
		private static final String AUTHOR = "author";
		private static final String AUTHORS = "authors";
		private static final String CONTRIBS = "contributors";
		private static final String DESC = "description";
		private static final String VERSION  = "version";
		private static final String SCREENS = "screenshots";
		private static final String ICON = "icon";
		private static final String SUPPORT = "support";

		private AppInfo info;

		public Deserializer(AppInfo info)
		{
			this.info = info;
		}

		@Override
		public AppInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		{
			try
			{
				info.name = convertToLocal(json.getAsJsonObject().get(NAME).getAsString());
				if(json.getAsJsonObject().has(AUTHOR))
					info.author = convertToLocal(json.getAsJsonObject().get(AUTHOR).getAsString());
				else if(json.getAsJsonObject().has(AUTHORS) && json.getAsJsonObject().get(AUTHORS).isJsonArray()){
					info.authors = context.deserialize(json.getAsJsonObject().get(AUTHORS), new TypeToken<String[]>(){}.getType());
				}
				if(json.getAsJsonObject().has(CONTRIBS) && json.getAsJsonObject().get(CONTRIBS).isJsonArray()){
					info.contributors = this.deserializeArray(json, CONTRIBS, context);
				}
				info.description = convertToLocal(json.getAsJsonObject().get(DESC).getAsString());
				info.version = json.getAsJsonObject().get(VERSION).getAsString();

				if(json.getAsJsonObject().has(SCREENS) && json.getAsJsonObject().get(SCREENS).isJsonArray())
				{
					info.screenshots = context.deserialize(json.getAsJsonObject().get(SCREENS), new TypeToken<String[]>(){}.getType());
				}

				if(json.getAsJsonObject().has(ICON) && json.getAsJsonObject().get(ICON).isJsonPrimitive())
				{
					info.icon = json.getAsJsonObject().get(ICON).getAsString();
				}

				if(json.getAsJsonObject().has(SUPPORT) && json.getAsJsonObject().get(SUPPORT).getAsJsonObject().size() > 0)
				{
					JsonObject supportObj = json.getAsJsonObject().get(SUPPORT).getAsJsonObject();
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

		private String[] deserializeArray(JsonElement json, String name, JsonDeserializationContext context){
			return context.deserialize(json.getAsJsonObject().get(name), new TypeToken<String[]>(){}.getType());
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
