package com.mrcrayfish.device.programs.system;

import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.renderer.ItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Settings;
import com.mrcrayfish.device.programs.system.component.Palette;
import com.mrcrayfish.device.programs.system.object.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.List;
import java.util.Stack;

public class ApplicationSettings extends SystemApplication
{
	private Button buttonPrevious;

	private Layout layoutMain;
	private Layout layoutGeneral;
	private CheckBox checkBoxShowApps;

	private Layout layoutPersonalise;
	private Button buttonWallpaperLeft;
	private Button buttonWallpaperRight;
	private Button buttonWallpaperUrl;

	private Layout layoutColorScheme;
	private Button buttonColorSchemeApply;

	private Stack<Layout> predecessor = new Stack<>();

	@Override
	public void init()
	{
		buttonPrevious = new Button(2, 2, Icons.ARROW_LEFT);
		buttonPrevious.setVisible(false);
		buttonPrevious.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				if(predecessor.size() > 0)
				{
					setCurrentLayout(predecessor.pop());
				}
				if(predecessor.isEmpty())
				{
					buttonPrevious.setVisible(false);
				}
			}
		});

		layoutMain = new Menu("Home");

		Button buttonColorScheme = new Button(5, 26, "Personalise", Icons.EDIT);
		buttonColorScheme.setSize(90, 20);
		buttonColorScheme.setToolTip("Personalise", "Change the wallpaper, UI colors, and more!");
		buttonColorScheme.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				showMenu(layoutPersonalise);
			}
		});
		layoutMain.addComponent(buttonColorScheme);

		layoutGeneral = new Menu("General");
		layoutGeneral.addComponent(buttonPrevious);

		checkBoxShowApps = new CheckBox("Show All Apps", 5, 5);
		checkBoxShowApps.setSelected(Settings.isShowAllApps());
		checkBoxShowApps.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			Settings.setShowAllApps(checkBoxShowApps.isSelected());
			Laptop laptop = getLaptop();
			laptop.getTaskBar().setupApplications(laptop.getApplications());
		});
		layoutGeneral.addComponent(checkBoxShowApps);

		layoutPersonalise = new Menu("Personalise");
		layoutPersonalise.addComponent(buttonPrevious);
		layoutPersonalise.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			int wallpaperX = 7;
			int wallpaperY = 28;
			Gui.drawRect(x + wallpaperX - 1, y + wallpaperY - 1, x + wallpaperX - 1 + 122, y + wallpaperY - 1 + 70, getLaptop().getSettings().getColorScheme().getHeaderColor());
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			List<ResourceLocation> wallpapers = getLaptop().getWallapapers();
			mc.getTextureManager().bindTexture(wallpapers.get(getLaptop().getCurrentWallpaper()));
			RenderUtil.drawRectWithFullTexture(x + wallpaperX, y + wallpaperY, 0, 0, 120, 68);
			mc.fontRenderer.drawString("Wallpaper", x + wallpaperX + 3, y + wallpaperY + 3, getLaptop().getSettings().getColorScheme().getTextColor(), true);
		});

		buttonWallpaperLeft = new Button(135, 27, Icons.ARROW_LEFT);
		buttonWallpaperLeft.setSize(25, 20);
		buttonWallpaperLeft.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton != 0)
				return;

			Laptop laptop = getLaptop();
			if(laptop != null)
			{
				laptop.prevWallpaper();
			}
        });
		layoutPersonalise.addComponent(buttonWallpaperLeft);

		buttonWallpaperRight = new Button(165, 27, Icons.ARROW_RIGHT);
		buttonWallpaperRight.setSize(25, 20);
		buttonWallpaperRight.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton != 0)
				return;

			Laptop laptop = getLaptop();
			if(laptop != null)
			{
				laptop.nextWallpaper();
			}
		});
		layoutPersonalise.addComponent(buttonWallpaperRight);

		buttonWallpaperUrl = new Button(135, 52, "Load", Icons.EARTH);
		buttonWallpaperUrl.setSize(55, 20);
		buttonWallpaperUrl.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton != 0)
				return;

			Dialog dialog = new Dialog.Message("This feature has not be added yet!");
			openDialog(dialog);
        });
		layoutPersonalise.addComponent(buttonWallpaperUrl);

		layoutColorScheme = new Menu("UI Colors");
		layoutPersonalise.addComponent(buttonPrevious);

		ComboBox.Custom<Integer> comboBoxTextColor = createColorPicker(145, 26);
		layoutColorScheme.addComponent(comboBoxTextColor);

		ComboBox.Custom<Integer> comboBoxTextSecondaryColor = createColorPicker(145, 44);
		layoutColorScheme.addComponent(comboBoxTextSecondaryColor);

		ComboBox.Custom<Integer> comboBoxHeaderColor = createColorPicker(145, 62);
		layoutColorScheme.addComponent(comboBoxHeaderColor);

		ComboBox.Custom<Integer> comboBoxBackgroundColor = createColorPicker(145, 80);
		layoutColorScheme.addComponent(comboBoxBackgroundColor);

		ComboBox.Custom<Integer> comboBoxBackgroundSecondaryColor = createColorPicker(145, 98);
		layoutColorScheme.addComponent(comboBoxBackgroundSecondaryColor);

		ComboBox.Custom<Integer> comboBoxItemBackgroundColor = createColorPicker(145, 116);
		layoutColorScheme.addComponent(comboBoxItemBackgroundColor);

		ComboBox.Custom<Integer> comboBoxItemHighlightColor = createColorPicker(145, 134);
		layoutColorScheme.addComponent(comboBoxItemHighlightColor);

		buttonColorSchemeApply = new Button(5, 79, Icons.CHECK);
		buttonColorSchemeApply.setEnabled(false);
		buttonColorSchemeApply.setToolTip("Apply", "Set these colors as the new color scheme");
		buttonColorSchemeApply.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				ColorScheme colorScheme = Laptop.getSystem().getSettings().getColorScheme();
				colorScheme.setBackgroundColor(comboBoxHeaderColor.getValue());
				buttonColorSchemeApply.setEnabled(false);
			}
		});
		layoutColorScheme.addComponent(buttonColorSchemeApply);

		setCurrentLayout(layoutMain);
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{

	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{

	}

	private void showMenu(Layout layout)
	{
		predecessor.push(getCurrentLayout());
		buttonPrevious.setVisible(true);
		setCurrentLayout(layout);
	}

	@Override
	public void onClose()
	{
		super.onClose();
		predecessor.clear();
	}

	private static class Menu extends Layout
	{
		private String title;

		public Menu(String title)
		{
			super(200, 150);
			this.title = title;
		}

		@Override
		public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
		{
			Gui.drawRect(x, y, x + width, y + 20, Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor());
			Gui.drawRect(x, y + 20, x + width, y + 21, Color.DARK_GRAY.getRGB());
			mc.fontRenderer.drawString(title, x + 22, y + 6, Color.WHITE.getRGB(), true);
			super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
		}
	}

	public ComboBox.Custom<Integer> createColorPicker(int left, int top)
	{
		ComboBox.Custom<Integer> colorPicker = new ComboBox.Custom<>(left, top, 50, 100, 100);
		colorPicker.setValue(Color.RED.getRGB());
		colorPicker.setItemRenderer(new ItemRenderer<Integer>()
		{
			@Override
			public void render(Integer integer, Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				if(integer != null)
				{
					Gui.drawRect(x, y, x + width, y + height, integer);
				}
			}
		});
		colorPicker.setChangeListener((oldValue, newValue) ->
		{
			buttonColorSchemeApply.setEnabled(true);
		});

		Palette palette = new Palette(5, 5, colorPicker);
		Layout layout = colorPicker.getLayout();
		layout.addComponent(palette);

		return colorPicker;
	}
}
