package com.mrcrayfish.device.core;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.System;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.client.LaptopFontRenderer;
import com.mrcrayfish.device.core.task.TaskInstallApp;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.SystemApplication;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
import com.mrcrayfish.device.programs.system.task.TaskUpdateApplicationData;
import com.mrcrayfish.device.programs.system.task.TaskUpdateSystemData;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

//TODO Intro message (created by mrcrayfish, donate here)

public class Laptop extends GuiScreen implements System
{
	public static final int ID = 1;

	private static final ResourceLocation LAPTOP_GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/laptop.png");

	public static final ResourceLocation ICON_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/atlas/app_icons.png");
	public static final int ICON_SIZE = 14;

	public static final FontRenderer fontRenderer = new LaptopFontRenderer(Minecraft.getMinecraft());

	private static final List<Application> APPLICATIONS = new ArrayList<>();
	private static final List<ResourceLocation> WALLPAPERS = new ArrayList<>();

	public static final int BORDER = 10;
	public static final int DEVICE_WIDTH = 384;
	public static final int DEVICE_HEIGHT = 216;
	public static final int SCREEN_WIDTH = DEVICE_WIDTH - BORDER * 2;
	public static final int SCREEN_HEIGHT = DEVICE_HEIGHT - BORDER * 2;

	private static System system;
	private static BlockPos pos;
	private static Drive mainDrive;

	private Settings settings;
	private TaskBar bar;
	private Window[] windows;
	private Layout context = null;

	private NBTTagCompound appData;
	private NBTTagCompound systemData;

	private int currentWallpaper;
	private int lastMouseX, lastMouseY;
	private boolean dragging = false;
	private boolean stretching = false;
	private boolean[] stretchDirections = new boolean[] { false, false, false, false };

	protected List<AppInfo> installedApps = new ArrayList<>();

	public Laptop(TileEntityLaptop laptop)
	{
		this.appData = laptop.getApplicationData();
		this.systemData = laptop.getSystemData();
		this.windows = new Window[5];
		this.settings = Settings.fromTag(systemData.getCompoundTag("Settings"));
		this.bar = new TaskBar(this);
		this.currentWallpaper = systemData.getInteger("CurrentWallpaper");
		if (currentWallpaper < 0 || currentWallpaper >= WALLPAPERS.size())
		{
			this.currentWallpaper = 0;
		}
		Laptop.system = this;
		Laptop.pos = laptop.getPos();
	}

	/**
	 * Returns the position of the laptop the player is currently using. This method can ONLY be called when the laptop GUI is open, otherwise it will return a null position.
	 *
	 * @return the position of the laptop currently in use
	 */
	@Nullable
	public static BlockPos getPos()
	{
		return pos;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		int posX = (width - DEVICE_WIDTH) / 2;
		int posY = (height - DEVICE_HEIGHT) / 2;
		bar.init(posX + BORDER, posY + DEVICE_HEIGHT - 28);

		installedApps.clear();
		NBTTagList tagList = systemData.getTagList("InstalledApps", Constants.NBT.TAG_STRING);
		for (int i = 0; i < tagList.tagCount(); i++)
		{
			AppInfo info = ApplicationManager.getApplication(tagList.getStringTagAt(i));
			if (info != null)
			{
				installedApps.add(info);
			}
		}
		installedApps.sort(AppInfo.SORT_NAME);
	}

	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);

		/* Close all windows and sendTask application data */
		for (Window window : windows)
		{
			if (window != null)
			{
				window.close();
			}
		}

		/* Send system data */
		this.updateSystemData();

		Laptop.pos = null;
		Laptop.system = null;
		Laptop.mainDrive = null;
	}

	private void updateSystemData()
	{
		systemData.setInteger("CurrentWallpaper", currentWallpaper);
		systemData.setTag("Settings", settings.toTag());

		NBTTagList tagListApps = new NBTTagList();
		installedApps.forEach(info -> tagListApps.appendTag(new NBTTagString(info.getFormattedId())));
		systemData.setTag("InstalledApps", tagListApps);

		TaskManager.sendTask(new TaskUpdateSystemData(pos, systemData));
	}

	@Override
	public void onResize(Minecraft mcIn, int width, int height)
	{
		super.onResize(mcIn, width, height);
		for (Window window : windows)
		{
			if (window != null)
			{
				window.content.markForLayoutUpdate();
			}
		}
	}

	@Override
	public void updateScreen()
	{
		bar.onTick();

		for (Window window : windows)
		{
			if (window != null)
			{
				window.onTick();
			}
		}

		FileBrowser.refreshList = false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		// Fixes the strange partialTicks that Forge decided to give us
		partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

		this.drawDefaultBackground();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(LAPTOP_GUI);

		/* Physical Screen */
		int posX = (width - DEVICE_WIDTH) / 2;
		int posY = (height - DEVICE_HEIGHT) / 2;

		/* Corners */
		this.drawTexturedModalRect(posX, posY, 0, 0, BORDER, BORDER); // TOP-LEFT
		this.drawTexturedModalRect(posX + DEVICE_WIDTH - BORDER, posY, 11, 0, BORDER, BORDER); // TOP-RIGHT
		this.drawTexturedModalRect(posX + DEVICE_WIDTH - BORDER, posY + DEVICE_HEIGHT - BORDER, 11, 11, BORDER, BORDER); // BOTTOM-RIGHT
		this.drawTexturedModalRect(posX, posY + DEVICE_HEIGHT - BORDER, 0, 11, BORDER, BORDER); // BOTTOM-LEFT

		/* Edges */
		RenderUtil.drawRectWithTexture(posX + BORDER, posY, 10, 0, SCREEN_WIDTH, BORDER, 1, BORDER); // TOP
		RenderUtil.drawRectWithTexture(posX + DEVICE_WIDTH - BORDER, posY + BORDER, 11, 10, BORDER, SCREEN_HEIGHT, BORDER, 1); // RIGHT
		RenderUtil.drawRectWithTexture(posX + BORDER, posY + DEVICE_HEIGHT - BORDER, 10, 11, SCREEN_WIDTH, BORDER, 1, BORDER); // BOTTOM
		RenderUtil.drawRectWithTexture(posX, posY + BORDER, 0, 11, BORDER, SCREEN_HEIGHT, BORDER, 1); // LEFT

		/* Center */
		RenderUtil.drawRectWithTexture(posX + BORDER, posY + BORDER, 10, 10, SCREEN_WIDTH, SCREEN_HEIGHT, 1, 1);

		/* Wallpaper */
		this.mc.getTextureManager().bindTexture(WALLPAPERS.get(currentWallpaper));
		RenderUtil.drawRectWithFullTexture(posX + 10, posY + 10, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		if (!MrCrayfishDeviceMod.DEVELOPER_MODE)
		{
			drawString(fontRenderer, "Alpha v" + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
		} else
		{
			drawString(fontRenderer, "Developer Version - " + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
		}

		boolean insideContext = false;
		if (context != null)
		{
			insideContext = GuiHelper.isMouseInside(mouseX, mouseY, context.xPosition, context.yPosition, context.xPosition + context.width, context.yPosition + context.height);
		}

		Image.CACHE.forEach((s, cachedImage) -> cachedImage.delete());

		/* Window */
		for (int i = windows.length - 1; i >= 0; i--)
		{
			Window window = windows[i];
			if (window != null)
			{
				window.render(this, mc, posX + BORDER, posY + BORDER, mouseX, mouseY, i == 0 && !insideContext, partialTicks);
			}
		}

		/* Application Bar */
		bar.render(this, mc, posX + 10, posY + DEVICE_HEIGHT - 28, mouseX, mouseY, partialTicks);

		if (context != null)
		{
			context.render(this, mc, context.xPosition, context.yPosition, mouseX, mouseY, true, partialTicks);
		}

		Image.CACHE.entrySet().removeIf(entry ->
		{
			Image.CachedImage cachedImage = entry.getValue();
			if (cachedImage.isDynamic() && cachedImage.isPendingDeletion())
			{
				int texture = cachedImage.getTextureId();
				if (texture != -1)
				{
					GL11.glDeleteTextures(texture);
				}
				return true;
			}
			return false;
		});

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;

		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;

		if (this.context != null)
		{
			int dropdownX = context.xPosition;
			int dropdownY = context.yPosition;
			if (GuiHelper.isMouseInside(mouseX, mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height))
			{
				this.context.handleMouseClick(mouseX, mouseY, mouseButton);
				return;
			} else
			{
				this.context = null;
			}
		}

		this.bar.handleClick(this, posX, posY + SCREEN_HEIGHT - TaskBar.BAR_HEIGHT, mouseX, mouseY, mouseButton);

		for (int i = 0; i < windows.length; i++)
		{
			Window<Application> window = windows[i];
			if (window != null)
			{
				Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
				if (isMouseWithinWindow(mouseX, mouseY, window) || isMouseWithinWindow(mouseX, mouseY, dialogWindow))
				{
					windows[i] = null;
					updateWindowStack();
					windows[0] = window;

					windows[0].handleMouseClick(this, posX, posY, mouseX, mouseY, mouseButton);

					boolean left = mouseX < posX + window.getOffsetX() + 1;
					boolean right = mouseX > posX + window.getOffsetX() + window.getWidth() - 2;
					boolean top = mouseY < posY + window.getOffsetY() + 1;
					boolean bottom = mouseY > posY + window.getOffsetY() + window.getHeight() - 2;

					if (left || right || top || bottom)
					{
						this.stretching = true;
						stretchDirections[0] = left;
						stretchDirections[1] = right;
						stretchDirections[2] = top;
						stretchDirections[3] = bottom;
						return;
					}

					if (isMouseWithinWindowBar(mouseX, mouseY, dialogWindow))
					{
						this.dragging = true;
						return;
					}

					if (isMouseWithinWindowBar(mouseX, mouseY, window) && window.content.isDecorated() && dialogWindow == null)
					{
						this.dragging = true;
						return;
					}
					break;
				}
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);
		this.dragging = false;
		this.stretching = false;
		if (this.context != null)
		{
			int dropdownX = context.xPosition;
			int dropdownY = context.yPosition;
			if (GuiHelper.isMouseInside(mouseX, mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height))
			{
				this.context.handleMouseRelease(mouseX, mouseY, state);
			}
		} else if (windows[0] != null)
		{
			windows[0].handleMouseRelease(mouseX, mouseY, state);
		}
	}

	@Override
	public void handleKeyboardInput() throws IOException
	{
		if (Keyboard.getEventKeyState())
		{
			char pressed = Keyboard.getEventCharacter();
			int code = Keyboard.getEventKey();

			if (windows[0] != null)
			{
				windows[0].handleKeyTyped(pressed, code);
			}

			super.keyTyped(pressed, code);
		} else
		{
			if (windows[0] != null)
			{
				windows[0].handleKeyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey());
			}
		}

		this.mc.dispatchKeypresses();
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
	{
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;

		if (this.context != null)
		{
			int dropdownX = context.xPosition;
			int dropdownY = context.yPosition;
			if (GuiHelper.isMouseInside(mouseX, mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height))
			{
				this.context.handleMouseDrag(mouseX, mouseY, clickedMouseButton);
			}
			return;
		}

		if (windows[0] != null)
		{
			Window<Application> window = windows[0];
			Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
			if (dragging)
			{
				if (isMouseOnScreen(mouseX, mouseY))
				{
					if (dialogWindow == null)
					{
						window.handleWindowMove(-(lastMouseX - mouseX), -(lastMouseY - mouseY));
					} else
					{
						dialogWindow.handleWindowMove(-(lastMouseX - mouseX), -(lastMouseY - mouseY));
					}
				} else
				{
					dragging = false;
				}
			} else if (stretching)
			{
				if (isMouseOnScreen(mouseX, mouseY))
				{
					int newX = 0;
					int newY = 0;
					int newWidth = 0;
					int newHeight = 0;

					int deltaX = (lastMouseX - mouseX);
					int deltaY = (lastMouseY - mouseY);

					boolean left = stretchDirections[0];
					boolean right = stretchDirections[1];
					boolean top = stretchDirections[2];
					boolean bottom = stretchDirections[3];

					if (left)
					{
						newX = deltaX;
						newWidth = deltaX;
					} else if (right)
					{
						newWidth = -deltaX;
					}

					if (top)
					{
						newY = deltaY;
						newHeight = deltaY;
					} else if (bottom)
					{
						newHeight = -deltaY;
					}
					
					if (dialogWindow == null)
					{
						if(window.resize(window.getWidth() + newWidth - 2, window.getHeight() + newHeight - 14))
						window.setPosition(window.getOffsetX() - newX, window.getOffsetY() - newY);
					} else
					{
						if(dialogWindow.resize(dialogWindow.getWidth() + newWidth - 2, dialogWindow.getHeight() + newHeight - 14))
						dialogWindow.setPosition(dialogWindow.getOffsetX() - newX, dialogWindow.getOffsetY() - newY);
					}
				} else
				{
					stretching = false;
				}
			} else
			{
				if (isMouseWithinWindow(mouseX, mouseY, window) || isMouseWithinWindow(mouseX, mouseY, dialogWindow))
				{
					window.handleMouseDrag(mouseX, mouseY, clickedMouseButton);
				}
			}
		}
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int scroll = Mouse.getEventDWheel();
		if (scroll != 0)
		{
			if (windows[0] != null)
			{
				windows[0].handleMouseScroll(mouseX, mouseY, scroll >= 0);
			}
		}
	}

	@Override
	public void drawHoveringText(List<String> textLines, int x, int y)
	{
		super.drawHoveringText(textLines, x, y);
	}

	public boolean sendApplicationToFront(AppInfo info)
	{
		for (int i = 0; i < windows.length; i++)
		{
			Window window = windows[i];
			if (window != null && window.content instanceof Application && ((Application) window.content).getInfo() == info)
			{
				windows[i] = null;
				updateWindowStack();
				windows[0] = window;
				return true;
			}
		}
		return false;
	}

	@Override
	public void openApplication(AppInfo info)
	{
		openApplication(info, (NBTTagCompound) null);
	}

	@Override
	public void openApplication(AppInfo info, NBTTagCompound intentTag)
	{
		Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
		optional.ifPresent(application -> openApplication(application, intentTag));
	}

	private void openApplication(Application app, NBTTagCompound intent)
	{
		if (!isApplicationInstalled(app.getInfo()))
			return;

		if (!isValidApplication(app.getInfo()))
			return;

		if (sendApplicationToFront(app.getInfo()))
			return;

		Window<Application> window = new Window<>(app, this);
		window.init((width - SCREEN_WIDTH) / 2, (height - SCREEN_HEIGHT) / 2, intent);

		if (appData.hasKey(app.getInfo().getFormattedId()))
		{
			app.load(appData.getCompoundTag(app.getInfo().getFormattedId()));
		}

		if (app instanceof SystemApplication)
		{
			((SystemApplication) app).setLaptop(this);
		}

		if (app.getCurrentLayout() == null)
		{
			app.restoreDefaultLayout();
		}

		window.setPosition((SCREEN_WIDTH - app.getWidth()) / 2, (SCREEN_HEIGHT - app.getHeight()) / 2 - TaskBar.BAR_HEIGHT);
		addWindow(window);

		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	@Override
	public boolean openApplication(AppInfo info, File file)
	{
		if (!isApplicationInstalled(info))
			return false;

		if (!isValidApplication(info))
			return false;

		Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
		if (optional.isPresent())
		{
			Application application = optional.get();
			boolean alreadyRunning = isApplicationRunning(info);
			openApplication(application, null);
			if (isApplicationRunning(info))
			{
				if (!application.handleFile(file))
				{
					if (!alreadyRunning)
					{
						closeApplication(application);
					}
					return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void closeApplication(AppInfo info)
	{
		Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
		optional.ifPresent(this::closeApplication);
	}

	private void closeApplication(Application app)
	{
		for (int i = 0; i < windows.length; i++)
		{
			Window<Application> window = windows[i];
			if (window != null)
			{
				if (window.content.getInfo().equals(app.getInfo()))
				{
					if (app.isDirty())
					{
						NBTTagCompound container = new NBTTagCompound();
						app.save(container);
						app.clean();
						appData.setTag(app.getInfo().getFormattedId(), container);
						TaskManager.sendTask(new TaskUpdateApplicationData(pos.getX(), pos.getY(), pos.getZ(), app.getInfo().getFormattedId(), container));
					}

					if (app instanceof SystemApplication)
					{
						((SystemApplication) app).setLaptop(null);
					}

					window.handleClose();
					windows[i] = null;
					return;
				}
			}
		}
	}

	private void addWindow(Window<Application> window)
	{
		if (hasReachedWindowLimit())
			return;

		updateWindowStack();
		windows[0] = window;
	}

	private void updateWindowStack()
	{
		for (int i = windows.length - 1; i >= 0; i--)
		{
			if (windows[i] != null)
			{
				if (i + 1 < windows.length)
				{
					if (i == 0 || windows[i - 1] != null)
					{
						if (windows[i + 1] == null)
						{
							windows[i + 1] = windows[i];
							windows[i] = null;
						}
					}
				}
			}
		}
	}

	private boolean hasReachedWindowLimit()
	{
		for (Window window : windows)
		{
			if (window == null)
				return false;
		}
		return true;
	}

	private boolean isMouseOnScreen(int mouseX, int mouseY)
	{
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;
		return GuiHelper.isMouseInside(mouseX, mouseY, posX, posY, posX + SCREEN_WIDTH, posY + SCREEN_HEIGHT);
	}

	private boolean isMouseWithinWindowBar(int mouseX, int mouseY, Window window)
	{
		if (window == null)
			return false;
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;
		return GuiHelper.isMouseInside(mouseX, mouseY, posX + window.getOffsetX() + 1, posY + window.getOffsetY() + 1, posX + window.getOffsetX() + window.getWidth() - 13, posY + window.getOffsetY() + 11);
	}

	private boolean isMouseWithinWindow(int mouseX, int mouseY, Window window)
	{
		if (window == null)
			return false;
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;
		return GuiHelper.isMouseInside(mouseX, mouseY, posX + window.getOffsetX(), posY + window.getOffsetY(), posX + window.getOffsetX() + window.getWidth(), posY + window.getOffsetY() + window.getHeight());
	}

	public boolean isMouseWithinApp(int mouseX, int mouseY, Window window)
	{
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;
		return GuiHelper.isMouseInside(mouseX, mouseY, posX + window.getOffsetX() + 1, posY + window.getOffsetY() + 13, posX + window.getOffsetX() + window.getWidth() - 1, posY + window.getOffsetY() + window.getHeight() - 1);
	}

	public boolean isApplicationRunning(AppInfo info)
	{
		for (Window window : windows)
		{
			if (window != null && ((Application) window.content).getInfo() == info)
			{
				return true;
			}
		}
		return false;
	}

	public void nextWallpaper()
	{
		if (currentWallpaper + 1 < WALLPAPERS.size())
		{
			currentWallpaper++;
		}
	}

	public void prevWallpaper()
	{
		if (currentWallpaper - 1 >= 0)
		{
			currentWallpaper--;
		}
	}

	public int getCurrentWallpaper()
	{
		return currentWallpaper;
	}

	public static void addWallpaper(ResourceLocation wallpaper)
	{
		if (wallpaper != null)
		{
			WALLPAPERS.add(wallpaper);
		}
	}

	public List<ResourceLocation> getWallapapers()
	{
		return ImmutableList.copyOf(WALLPAPERS);
	}

	@Nullable
	public Application getApplication(String appId)
	{
		return APPLICATIONS.stream().filter(app -> app.getInfo().getFormattedId().equals(appId)).findFirst().orElse(null);
	}

	@Override
	public List<AppInfo> getInstalledApplications()
	{
		return ImmutableList.copyOf(installedApps);
	}

	public boolean isApplicationInstalled(AppInfo info)
	{
		return info.isSystemApp() || installedApps.contains(info);
	}

	private boolean isValidApplication(AppInfo info)
	{
		if (MrCrayfishDeviceMod.proxy.hasAllowedApplications())
		{
			return MrCrayfishDeviceMod.proxy.getAllowedApplications().contains(info);
		}
		return true;
	}

	public void installApplication(AppInfo info, @Nullable Callback<Object> callback)
	{
		if (!isValidApplication(info))
			return;

		Task task = new TaskInstallApp(info, pos, true);
		task.setCallback((tagCompound, success) ->
		{
			if (success)
			{
				installedApps.add(info);
				installedApps.sort(AppInfo.SORT_NAME);
			}
			if (callback != null)
			{
				callback.execute(null, success);
			}
		});
		TaskManager.sendTask(task);
	}

	public void removeApplication(AppInfo info, @Nullable Callback<Object> callback)
	{
		if (!isValidApplication(info))
			return;

		Task task = new TaskInstallApp(info, pos, false);
		task.setCallback((tagCompound, success) ->
		{
			if (success)
			{
				installedApps.remove(info);
			}
			if (callback != null)
			{
				callback.execute(null, success);
			}
		});
		TaskManager.sendTask(task);
	}

	public static System getSystem()
	{
		return system;
	}

	public static void setMainDrive(Drive mainDrive)
	{
		if (Laptop.mainDrive == null)
		{
			Laptop.mainDrive = mainDrive;
		}
	}

	@Nullable
	public static Drive getMainDrive()
	{
		return mainDrive;
	}

	public List<Application> getApplications()
	{
		return APPLICATIONS;
	}

	public TaskBar getTaskBar()
	{
		return bar;
	}

	public Settings getSettings()
	{
		return settings;
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void openContext(Layout layout, int x, int y)
	{
		layout.updateComponents(x, y);
		context = layout;
		layout.init();
	}

	@Override
	public boolean hasContext()
	{
		return context != null;
	}

	@Override
	public void closeContext()
	{
		context = null;
		dragging = false;
	}
}
