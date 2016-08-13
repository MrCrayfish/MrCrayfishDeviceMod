package com.mrcrayfish.device.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.message.MessageSaveData;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;

public class Laptop extends GuiScreen 
{
	public static final int ID = 0;
	
	private static final ResourceLocation LAPTOP_GUI = new ResourceLocation("cdm:textures/gui/laptop.png");
	public static final List<ResourceLocation> WALLPAPERS = new ArrayList<ResourceLocation>();
	
	public static final int BORDER = 10;
	
	public static final int DEVICE_WIDTH = 384;
	public static final int DEVICE_HEIGHT = 216;

	public static final int SCREEN_WIDTH = DEVICE_WIDTH - BORDER * 2;
	public static final int SCREEN_HEIGHT = DEVICE_HEIGHT - BORDER * 2;

	private TaskBar bar;
	private Window[] windows;
	private NBTTagCompound data;
	
	public static int currentWallpaper;
	private int tileX, tileY, tileZ;
	private int lastMouseX, lastMouseY;
	
	private int draggingWindow;
	private boolean dragging = false;
	private boolean dirty = false;
	
	public Laptop(NBTTagCompound data, int tileX, int tileY, int tileZ)
	{
		this.data = data;
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileZ = tileZ;
		this.currentWallpaper = data.getInteger("CurrentWallpaper");
		if(currentWallpaper < 0 || currentWallpaper >= WALLPAPERS.size()) {
			this.currentWallpaper = 0;
		}
		this.windows = new Window[5];
	}
	
	@Override
	public void initGui() 
	{
		Keyboard.enableRepeatEvents(true);
		int posX = (width - DEVICE_WIDTH) / 2;
		int posY = (height - DEVICE_HEIGHT) / 2;
		bar = new TaskBar();
		bar.init(posX + BORDER, posY + DEVICE_HEIGHT - 28);
	}
	
	@Override
	public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        
        for(Window window : windows)
		{
        	if(window != null)
			{
        		closeApplication(window.app.getID());
			}
		}
        
        data.setInteger("CurrentWallpaper", this.currentWallpaper);
        
        if(dirty)
        {
        	PacketHandler.INSTANCE.sendToServer(new MessageSaveData(tileX, tileY, tileZ, data));
        }
        
        bar = null;
        windows = null;
        data = null;
    }
	
	@Override
	public void updateScreen() 
	{
		for(Window window : windows)
		{
			if(window != null)
			{
				window.onTick();
			}
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
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
		GuiHelper.drawModalRectWithUV(posX + BORDER, posY, 10, 0, SCREEN_WIDTH, BORDER, 1, BORDER); // TOP
		GuiHelper.drawModalRectWithUV(posX + DEVICE_WIDTH - BORDER, posY + BORDER, 11, 10, BORDER, SCREEN_HEIGHT, BORDER, 1); // RIGHT
		GuiHelper.drawModalRectWithUV(posX + BORDER, posY + DEVICE_HEIGHT - BORDER, 10, 11, SCREEN_WIDTH, BORDER, 1, BORDER); // BOTTOM
		GuiHelper.drawModalRectWithUV(posX, posY + BORDER, 0, 11, BORDER, SCREEN_HEIGHT, BORDER, 1); // LEFT
		
		/* Center */
		GuiHelper.drawModalRectWithUV(posX + BORDER, posY + BORDER, 10, 10, SCREEN_WIDTH, SCREEN_HEIGHT, 1, 1);
		
		/* Wallpaper */
		this.mc.getTextureManager().bindTexture(WALLPAPERS.get(currentWallpaper));
		GuiHelper.drawModalRectWithUV(posX + 10, posY + 10, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 256, 144);

		/* Window */
		for(int i = windows.length - 1; i >= 0; i--)
		{
			Window window = windows[i];
			if(window != null)
			{
				window.render(this, mc, getWindowX(window), getWindowY(window), mouseX, mouseY, i == 0, partialTicks);
			}
		}
		
		/* Application Bar */
		bar.render(this, mc, posX + 10, posY + DEVICE_HEIGHT - 28, mouseX, mouseY, partialTicks);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException 
	{
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
		
		int posX = (width - DEVICE_WIDTH) / 2;
		int posY = (height - DEVICE_HEIGHT) / 2;
		
		this.bar.handleClick(this, posX + 10, posY + DEVICE_HEIGHT - 28, mouseX, mouseY, mouseButton);
		
		for(int i = 0; i < windows.length; i++)
		{
			Window window = windows[i];
			if(window != null)
			{
				int windowX = getWindowX(window);
				int windowY = getWindowY(window);
				
				if(GuiHelper.isMouseInside(mouseX, mouseY, windowX + window.offsetX, windowY + window.offsetY, windowX + window.offsetX + window.width, windowY + window.offsetY + window.height))
				{
					windows[i] = null;
					updateWindowStack();
					windows[0] = window;
					
					windows[0].handleClick(this, windowX, windowY, mouseX, mouseY, mouseButton);
		
					if(mouseX >= windowX + window.offsetX + 1 && mouseX <= windowX + window.offsetX + window.width - 13)
					{
						if(mouseY >= windowY + window.offsetY + 1 && mouseY <= windowY + window.offsetY + 11)
						{
							this.dragging = true;
							return;
						}
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
		if(windows[0] != null)
		{
			windows[0].handleRelease(mouseX, mouseY, state);
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException 
	{
		if(windows[0] != null)
		{
			windows[0].handleKeyTyped(typedChar, keyCode);
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) 
	{
		int posX = (width - DEVICE_WIDTH) / 2;
		int posY = (height - DEVICE_HEIGHT) / 2;
		if(windows[0] != null)
		{
			if(dragging)
			{
				// Checks if mouse position in within the laptop's screen
				if(mouseX >= posX + 10 && mouseX <= posX + DEVICE_WIDTH - 20 && mouseY >= posY + 10 && mouseY <= posY + DEVICE_HEIGHT - 20)
				{
					windows[0].handleWindowMove(getWindowX(windows[0]), getWindowY(windows[0]), -(lastMouseX - mouseX), -(lastMouseY - mouseY), posX + 10, posY + 10);
				}
				else
				{
					dragging = false;
				}
			}
			else
			{
				Window window = windows[0];
				int windowX = getWindowX(windows[0]);
				int windowY = getWindowY(windows[0]);
				
				if(mouseX >= windowX + window.offsetX + 1 && mouseX <= windowX + window.width + window.offsetX - 1 && mouseY >= windowY + window.offsetY + 13 && mouseY <= windowY + window.offsetY + window.height - 1)
				{
					windows[0].handleDrag(mouseX, mouseY, clickedMouseButton);
				}
			}
		}
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException 
	{
		for(Window window : windows)
		{
			if(window != null)
			{
				window.handleButtonClick(this, button);
			}
		}
	}
	
	@Override
	public void drawHoveringText(List<String> textLines, int x, int y) 
	{
		super.drawHoveringText(textLines, x, y);
	}

	public void openApplication(Application app)
	{
		for(Window window : windows)
		{
			if(window != null)
			{
				if(window.app.getID().equals(app.getID()))
				{
					return;
				}
			}
		}
		
		Window window = new Window(app);
		window.init(buttonList, getWindowX(window), getWindowY(window));
		if(data.hasKey(app.getID()))
		{
			app.load(data.getCompoundTag(app.getID()));
		}
		addWindow(window);
		
	    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
	}
	
	public void closeApplication(String appId)
	{
		for(int i = 0; i < windows.length; i++)
		{
			Window window = windows[i];
			if(window != null)
			{
				if(window.app.getID().equals(appId))
				{
					if(window.save(data))
					{
						dirty = true;
					}
					window.handleClose(buttonList);
					windows[i] = null;
					window = null;
					return;
				}
			}
		}
	}
	
	public void addWindow(Window window)
	{
		if(hasReachedWindowLimit())
			return;

		updateWindowStack();
		windows[0] = window;
	}
	
	public void updateWindowStack()
	{
		for(int i = windows.length - 1; i >= 0; i--)
		{
			if(windows[i] == null)
			{
				continue;
			}
			else
			{
				if(i + 1 < windows.length)
				{
					if(i == 0 || windows[i - 1] != null)
					{
						if(windows[i + 1] == null)
						{
							windows[i + 1] = windows[i];
							windows[i] = null;
						}
					}
				}
			}
		}
	}
	
	public boolean hasReachedWindowLimit()
	{
		for(Window window : windows)
		{
			if(window == null) return false;
		}
		return true;
	}
	
	public int getWindowX(Window window)
	{
		if(window != null)
		{
			int posX = (width - DEVICE_WIDTH) / 2;
			return posX + (DEVICE_WIDTH - window.width) / 2;
		}
		return -1;
	}
	
	public int getWindowY(Window window)
	{
		if(window != null)
		{
			int posY = (height - DEVICE_HEIGHT) / 2;
			return posY + 10 + (DEVICE_HEIGHT - 38 - window.height) / 2;
		}
		return -1;
	}
	
	public static void nextWallpaper()
	{
		if(currentWallpaper + 1 < WALLPAPERS.size())
		{
			currentWallpaper++;
		}
	}
	
	public static void prevWallpaper()
	{
		if(currentWallpaper - 1 >= 0)
		{
			currentWallpaper--;
		}
	}
	
	public static void addWallpaper(ResourceLocation wallpaper)
	{
		if(wallpaper != null)
		{
			WALLPAPERS.add(wallpaper);
		}
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
