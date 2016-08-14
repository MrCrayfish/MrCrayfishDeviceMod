package com.mrcrayfish.device.app;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.gui.GuiButtonClose;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class Window
{
	public static final ResourceLocation WINDOW_GUI = new ResourceLocation("cdm:textures/gui/application.png");
	
	Application app;
	int width, height;
	int offsetX, offsetY;
	
	private GuiButton btnClose;
	
	public Window(Application app) 
	{
		this.app = app;
	}
	
	private void setWidth(int width) 
	{
		this.width = width + 2;
		if(this.width > Laptop.SCREEN_WIDTH)
		{
			this.width = Laptop.SCREEN_WIDTH;
		}
	}
	
	private void setHeight(int height) 
	{
		this.height = height + 14;
		if(this.height > 178)
		{
			this.height = 178;
		}
	}

	public void init(List<GuiButton> buttons, int x, int y)
	{
		btnClose = new GuiButtonClose(0, x + offsetX + width - 12, y + offsetY + 1);
		
		app.init(x + offsetX + 1, y + offsetY + 13);
	}
	
	public void onTick() 
	{
		app.onTick();
	}
	
	public void render(Laptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
	{	
		if(app.pendingLayoutUpdate)
		{
			this.setWidth(app.getWidth());
			this.setHeight(app.getHeight());
			this.offsetX = (Laptop.SCREEN_WIDTH - width) / 2;
			this.offsetY = (Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height) / 2;
			updateComponents(x, y);
			app.pendingLayoutUpdate = false;
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(WINDOW_GUI);

		/* Corners */
		gui.drawTexturedModalRect(x + offsetX, y + offsetY, 0, 0, 1, 13);
		gui.drawTexturedModalRect(x + offsetX + width - 13, y + offsetY, 2, 0, 13, 13);
		gui.drawTexturedModalRect(x + offsetX + width - 1, y + offsetY + height - 1, 14, 14, 1, 1);
		gui.drawTexturedModalRect(x + offsetX, y + offsetY + height - 1, 0, 14, 1, 1);
		
		/* Edges */
		GuiHelper.drawModalRectWithUV(x + offsetX + 1, y + offsetY, 1, 0, width - 14, 13, 1, 13);
		GuiHelper.drawModalRectWithUV(x + offsetX + width - 1, y + offsetY + 13, 14, 13, 1, height - 14, 1, 1);
		GuiHelper.drawModalRectWithUV(x + offsetX + 1, y + offsetY + height - 1, 1, 14, width - 2, 1, 13, 1);
		GuiHelper.drawModalRectWithUV(x + offsetX, y + offsetY + 13, 0, 13, 1, height - 14, 1, 1);
		
		/* Center */
		GuiHelper.drawModalRectWithUV(x + offsetX + 1, y + offsetY + 13, 1, 13, width - 2, height - 14, 13, 1);
		
		mc.fontRendererObj.drawString(app.getTitle(), x + offsetX + 3, y + offsetY + 3, Color.WHITE.getRGB(), true);
		
		btnClose.drawButton(mc, mouseX, mouseY);
		
		GlStateManager.disableBlend();

		app.render(gui, mc, x + offsetX + 1, y + offsetY + 13, mouseX, mouseY, active, partialTicks);
		
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public void handleButtonClick(Laptop laptop, GuiButton button) 
	{
		if(button.equals(btnClose))
		{
			app.restoreDefaultLayout();
			laptop.closeApplication(app.getID());
		}
	}
	
	public void handleClick(Laptop gui, int x, int y, int mouseX, int mouseY, int mouseButton)
	{
		if(btnClose.isMouseOver())
		{
			gui.closeApplication(app.getID());
			btnClose.playPressSound(gui.mc.getSoundHandler());
		}
		else
		{
			app.handleClick(mouseX, mouseY, mouseButton);
		}
	}
	
	public void handleKeyTyped(char character, int code)
	{
		app.handleKeyTyped(character, code);
	}
	
	public void handleWindowMove(int screenStartX, int screenStartY, int mouseDX, int mouseDY)
	{
		int newX = offsetX + mouseDX;
		int newY = offsetY + mouseDY;
		
		if(newX >= 0 && newX <= Laptop.SCREEN_WIDTH - width) 
		{
			this.offsetX = newX;
		} 
		else if(newX < 0) 
		{
			this.offsetX = 0;
		}
		else 
		{
			this.offsetX = Laptop.SCREEN_WIDTH - width;
		}
		
		if(newY >= 0 && newY <= Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height) 
		{
			this.offsetY = newY;
		} 
		else if(newY < 0) 
		{
			this.offsetY = 0;
		}
		else 
		{
			this.offsetY = Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height;
		}
		
		updateComponents(screenStartX, screenStartY);
	}
	
	public void handleDrag(int mouseX, int mouseY, int mouseButton)
	{
		app.handleDrag(mouseX, mouseY, mouseButton);
	}
	
	public void handleRelease(int mouseX, int mouseY, int mouseButton)
	{
		app.handleRelease(mouseX, mouseY, mouseButton);
	}
	
	public void handleClose(List<GuiButton> buttons)
	{
		buttons.remove(btnClose);
		app.onClose();
	}
	
	public void updateComponents(int x, int y)
	{
		app.updateComponents(x + offsetX + 1, y + offsetY + 13);
		btnClose.xPosition = x + offsetX + width - 12;
		btnClose.yPosition = y + offsetY + 1;
	}
	
	public String getAppId()
	{
		return this.app.getID();
	}
	
	public boolean save(NBTTagCompound tagCompound) 
	{
		if(app.isDirty())
		{
			NBTTagCompound container = new NBTTagCompound();
			app.save(container);
			tagCompound.setTag(app.getID(), container);
			return true;
		}
		return false;
	}
}
