package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.object.TrayItem;
import com.mrcrayfish.device.programs.system.component.AppGrid;
import com.mrcrayfish.device.programs.system.layout.LayoutAppPage;
import com.mrcrayfish.device.programs.system.layout.LayoutSearchApps;
import com.mrcrayfish.device.programs.system.layout.StandardLayout;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.awt.*;

public class ApplicationAppStore extends SystemApplication
{
	public static final int LAYOUT_WIDTH = 250;
	public static final int LAYOUT_HEIGHT = 150;

	private StandardLayout layoutMain;

	@Override
	public void init(@Nullable NBTTagCompound intent)
	{
		layoutMain = new StandardLayout("Home", LAYOUT_WIDTH, LAYOUT_HEIGHT, this, null);
		layoutMain.setIcon(Icons.HOME);

		Button btnSearch = new Button(214, 2, Icons.SEARCH);
		btnSearch.setToolTip("Search", "Find a specific application");
		btnSearch.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				this.setCurrentLayout(new LayoutSearchApps(this, getCurrentLayout()));
			}
        });
		layoutMain.addComponent(btnSearch);

		Button btnManageApps = new Button(232, 2, Icons.HAMMER);
		btnManageApps.setToolTip("Manage Apps", "Manage your installed applications");
		layoutMain.addComponent(btnManageApps);

		ScrollableLayout homePageLayout = new ScrollableLayout(0, 21, LAYOUT_WIDTH, 368, LAYOUT_HEIGHT - 21);
		homePageLayout.setScrollSpeed(10);
		homePageLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
			int offset = 60;
			Gui.drawRect(x, y + offset, x + LAYOUT_WIDTH, y + offset + 1, color.brighter().getRGB());
			Gui.drawRect(x, y + offset + 1, x + LAYOUT_WIDTH, y + offset + 19, color.getRGB());
			Gui.drawRect(x, y + offset + 19, x + LAYOUT_WIDTH, y + offset + 20, color.darker().getRGB());

			offset = 172;
			Gui.drawRect(x, y + offset, x + LAYOUT_WIDTH, y + offset + 1, color.brighter().getRGB());
			Gui.drawRect(x, y + offset + 1, x + LAYOUT_WIDTH, y + offset + 19, color.getRGB());
			Gui.drawRect(x, y + offset + 19, x + LAYOUT_WIDTH, y + offset + 20, color.darker().getRGB());
        });

		Image imageBanner = new Image(0, 0, LAYOUT_WIDTH, 60);
		imageBanner.setImage(new ResourceLocation(Reference.MOD_ID, "textures/gui/app_market_background.png"));
		imageBanner.setDrawFull(true);
		homePageLayout.addComponent(imageBanner);

		Label labelBanner = new Label("App Market", 10, 35);
		labelBanner.setScale(2);
		homePageLayout.addComponent(labelBanner);

		Label labelCertified = new Label(TextFormatting.WHITE + TextFormatting.BOLD.toString() + "Certified Apps", 10, 66);
		homePageLayout.addComponent(labelCertified);

		Label labelCertifiedDesc = new Label(TextFormatting.GRAY + "Verified by MrCrayfish", LAYOUT_WIDTH - 10, 66);
		labelCertifiedDesc.setAlignment(Component.ALIGN_RIGHT);
		labelCertifiedDesc.setScale(1.0);
		labelCertifiedDesc.setShadow(false);
		homePageLayout.addComponent(labelCertifiedDesc);

		AppGrid grid = new AppGrid(0, 81, 3, 1, ApplicationManager.getSystemApplications(), this);
		homePageLayout.addComponent(grid);

		Label labelOther = new Label(TextFormatting.WHITE + TextFormatting.BOLD.toString() + "Other Apps", 10, 178);
		homePageLayout.addComponent(labelOther);

		Label labelOtherDesc = new Label(TextFormatting.GRAY + "Community Created", LAYOUT_WIDTH - 10, 178);
		labelOtherDesc.setAlignment(Component.ALIGN_RIGHT);
		labelOtherDesc.setScale(1.0);
		labelOtherDesc.setShadow(false);
		homePageLayout.addComponent(labelOtherDesc);

		AppGrid other = new AppGrid(0, 192, 3, 2, ApplicationManager.getAvailableApplications(), this);
		homePageLayout.addComponent(other);

		layoutMain.addComponent(homePageLayout);

		this.setCurrentLayout(layoutMain);
	}

	@Override
	public void load(NBTTagCompound tagCompound) 
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) 
	{
		
	}

	public void openApplication(AppInfo info)
	{
		Layout layout = new LayoutAppPage(getLaptop(), info);
		this.setCurrentLayout(layout);
		Button btnPrevious = new Button(2, 2, Icons.ARROW_LEFT);
		btnPrevious.setClickListener((mouseX1, mouseY1, mouseButton1) ->
		{
			this.setCurrentLayout(layoutMain);
		});
		layout.addComponent(btnPrevious);
	}

	public static class StoreTrayItem extends TrayItem
	{
		public StoreTrayItem()
		{
			super(Icons.SHOP);
		}

		@Override
		public void handleClick(int mouseX, int mouseY, int mouseButton)
		{
			AppInfo info = ApplicationManager.getApplication("cdm:app_store");
			if(info != null)
			{
				Laptop.getSystem().openApplication(info);
			}
		}
	}
}
