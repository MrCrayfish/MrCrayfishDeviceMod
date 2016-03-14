package com.mrcrayfish.device.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.ApplicationBar;
import com.mrcrayfish.device.app.Window;
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

public class GuiLaptop extends GuiScreen 
{
	public static final int ID = 0;
	
	private static final ResourceLocation LAPTOP_GUI = new ResourceLocation("cdm:textures/gui/laptop.png");
	private static final List<ResourceLocation> WALLPAPERS = new ArrayList<ResourceLocation>();
	
	private int WIDTH = 384;
	private int HEIGHT = 216;

	private ApplicationBar bar;
	private Window window;
	private NBTTagCompound data;
	
	public static int currentWallpaper;
	private int tileX, tileY, tileZ;
	private int lastMouseX, lastMouseY;
	
	private boolean dragging = false;
	private boolean dirty = false;
	
	public GuiLaptop(NBTTagCompound data, int tileX, int tileY, int tileZ)
	{
		this.data = data;
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileZ = tileZ;
		this.currentWallpaper = data.getInteger("CurrentWallpaper");
		if(currentWallpaper < 0 || currentWallpaper >= WALLPAPERS.size()) {
			this.currentWallpaper = 0;
		}
	}
	
	@Override
	public void initGui() 
	{
		Keyboard.enableRepeatEvents(true);
		int posX = (width - WIDTH) / 2;
		int posY = (height - HEIGHT) / 2;
		bar = new ApplicationBar();
		bar.init(posX + 10, posY + HEIGHT - 28);
	}
	
	@Override
	public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        closeApplication();
        
        data.setInteger("CurrentWallpaper", this.currentWallpaper);
        
        if(dirty)
        {
        	PacketHandler.INSTANCE.sendToServer(new MessageSaveData(tileX, tileY, tileZ, data));
        }
        
        bar = null;
        window = null;
        data = null;
    }
	
	@Override
	public void updateScreen() 
	{
		if(this.window != null)
		{
			this.window.onTick();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		this.drawDefaultBackground();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(LAPTOP_GUI);
		
		/* Physical Screen */
		int posX = (width - WIDTH) / 2;
		int posY = (height - HEIGHT) / 2;
		this.drawTexturedModalRect(posX, posY, 0, 0, 256, 158);
		
		/* Corners */
		this.drawTexturedModalRect(posX, posY, 0, 0, 10, 10);
		this.drawTexturedModalRect(posX + WIDTH - 10, posY, 11, 0, 10, 10);
		this.drawTexturedModalRect(posX + WIDTH - 10, posY + HEIGHT - 10, 11, 11, 10, 10);
		this.drawTexturedModalRect(posX, posY + HEIGHT - 10, 0, 11, 10, 10);
		
		/* Edges */
		GuiHelper.drawModalRectWithUV(posX + 10, posY, 10, 0, WIDTH - 20, 10, 1, 10);
		GuiHelper.drawModalRectWithUV(posX + WIDTH - 10, posY + 10, 11, 10, 10, HEIGHT - 20, 10, 1);
		GuiHelper.drawModalRectWithUV(posX + 10, posY + HEIGHT - 10, 10, 11, WIDTH - 20, 10, 1, 10);
		GuiHelper.drawModalRectWithUV(posX, posY + 10, 0, 11, 10, HEIGHT - 20, 10, 1);
		
		/* Center */
		GuiHelper.drawModalRectWithUV(posX + 10, posY + 10, 10, 10, WIDTH - 20, HEIGHT - 20, 1, 1);
		
		/* Wallpaper */
		this.mc.getTextureManager().bindTexture(WALLPAPERS.get(currentWallpaper));
		GuiHelper.drawModalRectWithUV(posX + 10, posY + 10, 0, 0, WIDTH - 20, HEIGHT - 20, 256, 144);

		/* Window */
		if(this.window != null)
		{
			window.render(this, mc, getWindowX(), getWindowY(), mouseX, mouseY);
		}
		
		/* Application Bar */
		bar.render(this, mc, posX + 10, posY + HEIGHT - 28, mouseX, mouseY);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException 
	{
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
		
		int posX = (width - WIDTH) / 2;
		int posY = (height - HEIGHT) / 2;
		
		this.bar.handleClick(this, posX + 10, posY + HEIGHT - 28, mouseX, mouseY, mouseButton);
		
		if(window != null)
		{
			int windowX = getWindowX();
			int windowY = getWindowY();
			
			this.window.handleClick(this, windowX, windowY, mouseX, mouseY, mouseButton);

			if(mouseX >= windowX + window.offsetX + 1 && mouseX <= windowX + window.offsetX + window.width - 13)
			{
				if(mouseY >= windowY + window.offsetY + 1 && mouseY <= windowY + window.offsetY + 11)
				{
					this.dragging = true;
					return;
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
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException 
	{
		if(window != null)
		{
			this.window.handleKeyTyped(typedChar, keyCode);
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) 
	{
		int posX = (width - WIDTH) / 2;
		int posY = (height - HEIGHT) / 2;
		if(dragging && window != null)
		{
			if(mouseX >= posX + 10 && mouseX <= posX + WIDTH - 20 && mouseY >= posY + 10 && mouseY <= posY + HEIGHT - 20)
			{
				window.handleDrag(this, getWindowX(), getWindowY(), -(lastMouseX - mouseX), -(lastMouseY - mouseY), posX + 10, posY + 10);
			}
			else
			{
				dragging = false;
			}
		}
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException 
	{
		if(window != null)
		{
			window.handleButtonClick(this, button);
		}
	}
	
	@Override
	public void drawHoveringText(List<String> textLines, int x, int y) 
	{
		super.drawHoveringText(textLines, x, y);
	}
	
	public void openApplication(Application app)
	{
		closeApplication();
		window = new Window(app);
		window.init(buttonList, getWindowX(), getWindowY());
		if(data.hasKey(app.getID()))
		{
			app.load(data.getCompoundTag(app.getID()));
		}
	    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
	}
	
	public void closeApplication()
	{
		if(window != null)
		{
			if(window.save(data))
			{
				dirty = true;
			}
			window.handleClose(buttonList);
			window = null;
		}
	}
	
	public int getWindowX()
	{
		if(window != null)
		{
			int posX = (width - WIDTH) / 2;
			return posX + (WIDTH - window.width) / 2;
		}
		return -1;
	}
	
	public int getWindowY()
	{
		if(window != null)
		{
			int posY = (height - HEIGHT) / 2;
			return posY + 10 + (HEIGHT - 38 - window.height) / 2;
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
