package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.layout.LayoutAppPage;
import com.mrcrayfish.device.programs.system.layout.LayoutSearchApps;
import com.mrcrayfish.device.programs.system.layout.StandardLayout;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

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

	private void openApplication(AppInfo info)
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
}
