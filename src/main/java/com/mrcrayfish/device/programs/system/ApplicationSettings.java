package com.mrcrayfish.device.programs.system;

import java.awt.Color;
import java.util.List;
import java.util.Stack;

import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.renderer.ItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Settings;
import com.mrcrayfish.device.programs.system.component.Palette;
import com.mrcrayfish.device.programs.system.object.ColourScheme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ApplicationSettings extends SystemApplication {
	private Button buttonPrevious;

	private Layout layoutMain;
	private Layout layoutGeneral;
	private CheckBox checkBoxShowApps;

	private Layout layoutPersonalise;
	private Button buttonWallpaperLeft;
	private Button buttonWallpaperRight;
	private Button buttonWallpaperUrl;

	private Layout layoutColourScheme;
	private Button buttonColourSchemeApply;

	private Stack<Layout> predecessor = new Stack<>();

	@Override
	public void init() {
		buttonPrevious = new Button(2, 2, Icons.ARROW_LEFT);
		buttonPrevious.setVisible(false);
		buttonPrevious.setClickListener((c, mouseButton) -> {
			if (mouseButton == 0) {
				if (predecessor.size() > 0) {
					setCurrentLayout(predecessor.pop());
				}
				if (predecessor.isEmpty()) {
					buttonPrevious.setVisible(false);
				}
			}
		});

		layoutMain = new Menu("Home");

		Button buttonColourScheme = new Button(5, 26, "Personalise", Icons.EDIT);
		buttonColourScheme.setSize(90, 20);
		buttonColourScheme.setToolTip("Personalise", "Change the wallpaper, UI colours, and more!");
		buttonColourScheme.setClickListener((c, mouseButton) -> {
			if (mouseButton == 0) {
				showMenu(layoutPersonalise);
			}
		});
		layoutMain.addComponent(buttonColourScheme);

		layoutGeneral = new Menu("General");
		layoutGeneral.addComponent(buttonPrevious);

		checkBoxShowApps = new CheckBox("Show All Apps", 5, 5);
		checkBoxShowApps.setSelected(Settings.isShowAllApps());
		checkBoxShowApps.setClickListener((c, mouseButton) -> {
			Settings.setShowAllApps(checkBoxShowApps.isSelected());
			Laptop laptop = getLaptop();
			laptop.getTaskBar().setupApplications(laptop.getApplications());
		});
		layoutGeneral.addComponent(checkBoxShowApps);

		layoutPersonalise = new Menu("Personalise");
		layoutPersonalise.addComponent(buttonPrevious);
		layoutPersonalise.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
			int wallpaperX = 7;
			int wallpaperY = 28;
			Gui.drawRect((x + wallpaperX) - 1, (y + wallpaperY) - 1, ((x + wallpaperX) - 1) + 122,
					((y + wallpaperY) - 1) + 70, getLaptop().getSettings().getColourScheme().getHeaderColour());
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			List<ResourceLocation> wallpapers = getLaptop().getWallapapers();
			mc.getTextureManager().bindTexture(wallpapers.get(getLaptop().getCurrentWallpaper()));
			RenderUtil.drawRectWithFullTexture(x + wallpaperX, y + wallpaperY, 0, 0, 120, 68);
			mc.fontRenderer.drawString("Wallpaper", x + wallpaperX + 3, y + wallpaperY + 3,
					getLaptop().getSettings().getColourScheme().getTextColour(), true);
		});

		buttonWallpaperLeft = new Button(135, 27, Icons.ARROW_LEFT);
		buttonWallpaperLeft.setSize(25, 20);
		buttonWallpaperLeft.setClickListener((c, mouseButton) -> {
			if (mouseButton != 0) {
				return;
			}

			Laptop laptop = getLaptop();
			if (laptop != null) {
				laptop.prevWallpaper();
			}
		});
		layoutPersonalise.addComponent(buttonWallpaperLeft);

		buttonWallpaperRight = new Button(165, 27, Icons.ARROW_RIGHT);
		buttonWallpaperRight.setSize(25, 20);
		buttonWallpaperRight.setClickListener((c, mouseButton) -> {
			if (mouseButton != 0) {
				return;
			}

			Laptop laptop = getLaptop();
			if (laptop != null) {
				laptop.nextWallpaper();
			}
		});
		layoutPersonalise.addComponent(buttonWallpaperRight);

		buttonWallpaperUrl = new Button(135, 52, "Load", Icons.EARTH);
		buttonWallpaperUrl.setSize(55, 20);
		buttonWallpaperUrl.setClickListener((c, mouseButton) -> {
			if (mouseButton != 0) {
				return;
			}

			Dialog dialog = new Dialog.Message("This feature has not be added yet!");
			openDialog(dialog);
		});
		layoutPersonalise.addComponent(buttonWallpaperUrl);

		layoutColourScheme = new Menu("UI Colours");
		layoutPersonalise.addComponent(buttonPrevious);

		ComboBox.Custom<Integer> comboBoxTextColour = createColourPicker(145, 26);
		layoutColourScheme.addComponent(comboBoxTextColour);

		ComboBox.Custom<Integer> comboBoxTextSecondaryColour = createColourPicker(145, 44);
		layoutColourScheme.addComponent(comboBoxTextSecondaryColour);

		ComboBox.Custom<Integer> comboBoxHeaderColour = createColourPicker(145, 62);
		layoutColourScheme.addComponent(comboBoxHeaderColour);

		ComboBox.Custom<Integer> comboBoxBackgroundColour = createColourPicker(145, 80);
		layoutColourScheme.addComponent(comboBoxBackgroundColour);

		ComboBox.Custom<Integer> comboBoxBackgroundSecondaryColour = createColourPicker(145, 98);
		layoutColourScheme.addComponent(comboBoxBackgroundSecondaryColour);

		ComboBox.Custom<Integer> comboBoxItemBackgroundColour = createColourPicker(145, 116);
		layoutColourScheme.addComponent(comboBoxItemBackgroundColour);

		ComboBox.Custom<Integer> comboBoxItemHighlightColour = createColourPicker(145, 134);
		layoutColourScheme.addComponent(comboBoxItemHighlightColour);

		buttonColourSchemeApply = new Button(5, 79, Icons.CHECK);
		buttonColourSchemeApply.setEnabled(false);
		buttonColourSchemeApply.setToolTip("Apply", "Set these colours as the new colour scheme");
		buttonColourSchemeApply.setClickListener((c, mouseButton) -> {
			if (mouseButton == 0) {
				ColourScheme colourScheme = Laptop.getSystem().getSettings().getColourScheme();
				colourScheme.setBackgroundColour(comboBoxHeaderColour.getValue());
				buttonColourSchemeApply.setEnabled(false);
			}
		});
		layoutColourScheme.addComponent(buttonColourSchemeApply);

		setCurrentLayout(layoutMain);
	}

	@Override
	public void load(NBTTagCompound tagCompound) {

	}

	@Override
	public void save(NBTTagCompound tagCompound) {

	}

	private void showMenu(Layout layout) {
		predecessor.push(getCurrentLayout());
		buttonPrevious.setVisible(true);
		setCurrentLayout(layout);
	}

	@Override
	public void onClose() {
		super.onClose();
		predecessor.clear();
	}

	private static class Menu extends Layout {
		private String title;

		public Menu(String title) {
			super(200, 150);
			this.title = title;
		}

		@Override
		public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive,
				float partialTicks) {
			Gui.drawRect(x, y, x + width, y + 20,
					Laptop.getSystem().getSettings().getColourScheme().getBackgroundColour());
			Gui.drawRect(x, y + 20, x + width, y + 21, Color.DARK_GRAY.getRGB());
			mc.fontRenderer.drawString(title, x + 22, y + 6, Color.WHITE.getRGB(), true);
			super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
		}
	}

	public ComboBox.Custom<Integer> createColourPicker(int left, int top) {
		ComboBox.Custom<Integer> colourPicker = new ComboBox.Custom<>(left, top, 50, 100, 100);
		colourPicker.setValue(Color.RED.getRGB());
		colourPicker.setItemRenderer(new ItemRenderer<Integer>() {
			@Override
			public void render(Integer integer, Gui gui, Minecraft mc, int x, int y, int width, int height) {
				if (integer != null) {
					Gui.drawRect(x, y, x + width, y + height, integer);
				}
			}
		});
		colourPicker.setChangeListener((oldValue, newValue) -> {
			buttonColourSchemeApply.setEnabled(true);
		});

		Palette palette = new Palette(5, 5, colourPicker);
		Layout layout = colourPicker.getLayout();
		layout.addComponent(palette);

		return colourPicker;
	}
}
