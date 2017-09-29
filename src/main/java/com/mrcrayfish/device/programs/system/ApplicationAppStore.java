package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ApplicationAppStore extends Application
{
	private static final ResourceLocation BANNER = new ResourceLocation("cdm:textures/app/app_store_banner.png");

	private Layout layoutHome;
	private Image imageBanner;
	private Label labelBanner;
	private Label labelCategories;
	private ItemList<String> itemListCategories;
	
	public ApplicationAppStore() 
	{
		//super("app_market", "App Market");
		this.setDefaultWidth(250);
		this.setDefaultHeight(150);
	}

	@Override
	public void init()
	{
		layoutHome = new Layout(250, 159);
		layoutHome.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			Gui.drawRect(x, y + 50, x + width, y + 51, Color.DARK_GRAY.getRGB());
			Gui.drawRect(x, y + 51, x + width, y + 63, Color.DARK_GRAY.getRGB());
        });

		//imageBanner = new Image(0, 0, 250, 50, 0, 0, 512, 141, BANNER);
		imageBanner = new Image(0, 0, 250, 50, "https://i.imgur.com/VAGCpKY.jpg");
		layoutHome.addComponent(imageBanner);

		labelBanner = new Label("App Market", 10, 42);
		labelBanner.setScale(2);
		layoutHome.addComponent(labelBanner);

		labelCategories = new Label("Categories", 5, 70);
		layoutHome.addComponent(labelCategories);

		itemListCategories = new ItemList<>(5, 82, 100, 5, true);
		itemListCategories.addItem("Games");
		itemListCategories.addItem("Tools");
		itemListCategories.addItem("Education");
		itemListCategories.addItem("Entertainment");
		itemListCategories.addItem("Sports");
		itemListCategories.addItem("VR");
		itemListCategories.addItem("Finance");
		itemListCategories.addItem("Multiplayer");
		itemListCategories.addItem("Shopping");
		itemListCategories.addItem("Social");
		layoutHome.addComponent(itemListCategories);

		this.setCurrentLayout(layoutHome);

		/*appsLabel = new Label("Application List", 5, 5);
		super.addComponent(appsLabel);
		
		apps = new ItemList<AppInfo>(5, 18, 100, 6);
		apps.addItem(new AppInfo("Blah"));
		apps.addItem(new AppInfo("Blah"));
		apps.addItem(new AppInfo("Blah"));
		apps.setListItemRenderer(new ListItemRenderer<AppInfo>(20) {
			@Override
			public void render(AppInfo e, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {
				if(selected)
					gui.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.getRGB());
				else
					gui.drawRect(x, y, x + width, y + height, Color.GRAY.getRGB());
				e.renderIcon(mc, x + 3, y + 3);
				gui.drawString(mc.fontRendererObj, e.getFormattedId(), x + 20, y + 6, Color.WHITE.getRGB());
				
			}
		});
		apps.setItemClickListener((info, index, mouseButton) ->
		{

        });
		super.addComponent(apps);*/
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
