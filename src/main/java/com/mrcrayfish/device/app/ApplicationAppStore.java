package com.mrcrayfish.device.app;

import java.awt.Color;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.CheckBox;
import com.mrcrayfish.device.app.components.ItemList;
import com.mrcrayfish.device.app.components.Label;
import com.mrcrayfish.device.app.components.Slider;
import com.mrcrayfish.device.app.components.Text;
import com.mrcrayfish.device.app.listener.ClickListener;
import com.mrcrayfish.device.app.listener.SlideListener;
import com.mrcrayfish.device.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.object.AppInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationAppStore extends Application
{
	private Label appsLabel;
	private ItemList<AppInfo> apps;
	
	private Label appTitle;
	private Label appAuthor;
	private Text appDescription;
	private Button btnInstall;
	
	public ApplicationAppStore() 
	{
		super("app_store", "App Store");
		this.setDefaultWidth(250);
		this.setDefaultHeight(150);
	}

	@Override
	public void init(int x, int y)
	{
		super.init(x, y);
		
		appsLabel = new Label("Application List", x, y, 5, 5);
		super.addComponent(appsLabel);
		
		apps = new ItemList<AppInfo>(x, y, 5, 18, 100, 6);
		apps.addItem(new AppInfo("Blah"));
		apps.addItem(new AppInfo("Blah"));
		apps.addItem(new AppInfo("Blah"));
		apps.setListItemRenderer(new ListItemRenderer<AppInfo>(20) {
			@Override
			public void render(AppInfo e, Gui gui, Minecraft mc, int x, int y, int width, boolean selected) {
				if(selected)
					gui.drawRect(x, y, x + width, y + getHeight(), Color.DARK_GRAY.getRGB());
				else
					gui.drawRect(x, y, x + width, y + getHeight(), Color.GRAY.getRGB());
				e.renderIcon(mc, x + 3, y + 3);
				gui.drawString(mc.fontRendererObj, e.toString(), x + 20, y + 6, Color.WHITE.getRGB());
				
			}
		});
		apps.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				AppInfo info = apps.getSelectedItem();
				if(info != null)
				{
					appTitle.setText(info.getName());
					appAuthor.setText(info.getAuthor());
					appDescription.setText(info.getDescription());
					btnInstall.enabled = true;
				}
				else
				{
					appTitle.setText("-");
					appAuthor.setText("-");
					appDescription.setText("-");
					btnInstall.enabled = false;
				}
			}
		});
		super.addComponent(apps);
		
		appTitle = new Label("", x, y, 130, 5);
		super.addComponent(appTitle);
		
		appAuthor = new Label("", x, y, 130, 16);
		super.addComponent(appAuthor);
		
		appDescription = new Text("", Minecraft.getMinecraft().fontRendererObj, x, y, 130, 35, 100);
		super.addComponent(appDescription);
		
		btnInstall = new Button("Install", x, y, 125, 100, 100, 20);
		super.addComponent(btnInstall);
	}

	@Override
	public void render(Gui gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active) 
	{
		super.render(gui, mc, x, y, mouseX, mouseY, active);
		gui.drawRect(x + 125, y + 30, x + 225, y + 31, Color.DARK_GRAY.getRGB());
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
