package com.mrcrayfish.device.core;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.gui.GuiButtonClose;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class Window<T extends Wrappable>
{
	public static final ResourceLocation WINDOW_GUI = new ResourceLocation("cdm:textures/gui/application.png");
	
	private static final int COLOUR_WINDOW_DARK = new Color(0F, 0F, 0F, 0.25F).getRGB();
	
	T content;
	int width, height;
	int offsetX, offsetY;
	
	Window<Dialog> dialogWindow = null;
	
	protected GuiButton btnClose;
	
	public Window(T wrappable) 
	{
		this.content = wrappable;
	}
	
	protected void setWidth(int width) 
	{
		this.width = width + 2;
		if(this.width > Laptop.SCREEN_WIDTH)
		{
			this.width = Laptop.SCREEN_WIDTH;
		}
	}
	
	protected void setHeight(int height) 
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
		
		if(content instanceof Application)
		{
			((Application) content).setWindow(this);
		}
		
		content.init(x + offsetX + 1, y + offsetY + 13);
	}
	
	public void onTick() 
	{
		if(dialogWindow != null)
		{
			dialogWindow.onTick();
		}
		content.onTick();
	}
	
	public void render(Laptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
	{	
		if(content.isPendingLayoutUpdate())
		{
			this.setWidth(content.getWidth());
			this.setHeight(content.getHeight());
			this.offsetX = (Laptop.SCREEN_WIDTH - width) / 2;
			this.offsetY = (Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height) / 2;
			updateComponents(x, y);
			content.clearPendingLayout();
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
		
		mc.fontRendererObj.drawString(content.getTitle(), x + offsetX + 3, y + offsetY + 3, Color.WHITE.getRGB(), true);
		
		btnClose.drawButton(mc, mouseX, mouseY);
		
		GlStateManager.disableBlend();

		content.render(gui, mc, x + offsetX + 1, y + offsetY + 13, mouseX, mouseY, active && dialogWindow == null, partialTicks);
		
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
		if(dialogWindow != null)
		{
			gui.drawRect(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height, COLOUR_WINDOW_DARK);
			dialogWindow.render(gui, mc, x, y, mouseX, mouseY, active, partialTicks);
		}
	}
	
	public void handleButtonClick(Laptop laptop, GuiButton button) 
	{
		if(button.equals(btnClose))
		{
			laptop.close((Application) content);
		}
	}
	
	public void handleClick(Laptop gui, int x, int y, int mouseX, int mouseY, int mouseButton)
	{
		if(dialogWindow != null)
		{
			dialogWindow.handleClick(gui, x, y, mouseX, mouseY, mouseButton);
			return;
		}
		
		if(btnClose.isMouseOver())
		{
			gui.close((Application) content);
			btnClose.playPressSound(gui.mc.getSoundHandler());
		}
		else
		{
			content.handleClick(mouseX, mouseY, mouseButton);	
		}
	}
	
	public void handleKeyTyped(char character, int code)
	{
		if(dialogWindow != null)
		{
			dialogWindow.handleKeyTyped(character, code);
			return;
		}
		content.handleKeyTyped(character, code);
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
		if(dialogWindow != null)
		{
			dialogWindow.handleDrag(mouseX, mouseY, mouseButton);
			return;
		}
		content.handleDrag(mouseX, mouseY, mouseButton);
	}
	
	public void handleRelease(int mouseX, int mouseY, int mouseButton)
	{
		if(dialogWindow != null)
		{
			dialogWindow.handleRelease(mouseX, mouseY, mouseButton);
			return;
		}
		content.handleRelease(mouseX, mouseY, mouseButton);
	}
	
	public void handleClose()
	{
		content.onClose();
	}
	
	public void updateComponents(int x, int y)
	{
		content.updateComponents(x + offsetX + 1, y + offsetY + 13);
		btnClose.xPosition = x + offsetX + width - 12;
		btnClose.yPosition = y + offsetY + 1;
	}
	
	public void openDialog(Dialog dialog)
	{
		this.dialogWindow = new DialogWindow(this, dialog);
		this.dialogWindow.init(null, 0, 0);
	}
	
	public void closeDialog()
	{
		if(dialogWindow != null)
		{
			dialogWindow.handleClose();
			dialogWindow = null;
		}
	}
	
	public static class DialogWindow extends Window
	{
		private Window parent;
		
		public DialogWindow(Window parent, Dialog dialog)
		{
			super(dialog);
			this.parent = parent;
			dialog.setWindow(this);
		}
		
		@Override
		public void handleClick(Laptop gui, int x, int y, int mouseX, int mouseY, int mouseButton)
		{
			content.handleClick(mouseX, mouseY, mouseButton);

			if(btnClose.isMouseOver())
			{
				parent.closeDialog();
			}
		}
		
		@Override
		public void closeDialog()
		{
			parent.closeDialog();
		}
	}
}
