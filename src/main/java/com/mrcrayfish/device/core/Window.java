package com.mrcrayfish.device.core;

import java.awt.Color;

import com.mrcrayfish.device.util.GLHelper;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.gui.GuiButtonClose;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Window<T extends Wrappable>
{
	public static final ResourceLocation WINDOW_GUI = new ResourceLocation("cdm:textures/gui/application.png");
	
	public static final int COLOUR_WINDOW_DARK = new Color(0F, 0F, 0F, 0.25F).getRGB();
	
	T content;
	int width, height;
	int offsetX, offsetY;

	final Laptop laptop;
	Window<Dialog> dialogWindow = null;
	Window<? extends  Wrappable> parent = null;
	
	protected GuiButton btnClose;
	
	public Window(T wrappable, Laptop laptop)
	{
		this.content = wrappable;
		this.laptop = laptop;
		wrappable.setWindow(this);
	}
	
	void setWidth(int width)
	{
		this.width = width + 2;
		if(this.width > Laptop.SCREEN_WIDTH)
		{
			this.width = Laptop.SCREEN_WIDTH;
		}
	}
	
	void setHeight(int height)
	{
		this.height = height + 14;
		if(this.height > 178)
		{
			this.height = 178;
		}
	}

	void init(int x, int y)
	{
		btnClose = new GuiButtonClose(0, x + offsetX + width - 12, y + offsetY + 1);
		content.init();
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
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(WINDOW_GUI);

		/* Corners */
		gui.drawTexturedModalRect(x + offsetX, y + offsetY, 0, 0, 1, 13);
		gui.drawTexturedModalRect(x + offsetX + width - 13, y + offsetY, 2, 0, 13, 13);
		gui.drawTexturedModalRect(x + offsetX + width - 1, y + offsetY + height - 1, 14, 14, 1, 1);
		gui.drawTexturedModalRect(x + offsetX, y + offsetY + height - 1, 0, 14, 1, 1);
		
		/* Edges */
		RenderUtil.drawRectWithTexture(x + offsetX + 1, y + offsetY, 1, 0, width - 14, 13, 1, 13);
		RenderUtil.drawRectWithTexture(x + offsetX + width - 1, y + offsetY + 13, 14, 13, 1, height - 14, 1, 1);
		RenderUtil.drawRectWithTexture(x + offsetX + 1, y + offsetY + height - 1, 1, 14, width - 2, 1, 13, 1);
		RenderUtil.drawRectWithTexture(x + offsetX, y + offsetY + 13, 0, 13, 1, height - 14, 1, 1);
		
		/* Center */
		RenderUtil.drawRectWithTexture(x + offsetX + 1, y + offsetY + 13, 1, 13, width - 2, height - 14, 13, 1);

		String windowTitle = content.getWindowTitle();
		if(mc.fontRenderer.getStringWidth(windowTitle) > width - 2 - 13 - 3) // window width, border, close button, padding, padding
		{
			windowTitle = mc.fontRenderer.trimStringToWidth(windowTitle, width - 2 - 13 - 3);
		}
		mc.fontRenderer.drawString(windowTitle, x + offsetX + 3, y + offsetY + 3, Color.WHITE.getRGB(), true);
		
		btnClose.drawButton(mc, mouseX, mouseY, partialTicks);
		
		GlStateManager.disableBlend();

		/* Render content */
		content.render(gui, mc, x + offsetX + 1, y + offsetY + 13, mouseX, mouseY, active && dialogWindow == null, partialTicks);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
		if(dialogWindow != null)
		{
			Gui.drawRect(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height, COLOUR_WINDOW_DARK);
			dialogWindow.render(gui, mc, x, y, mouseX, mouseY, active, partialTicks);
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
	
	public void handleKeyReleased(char character, int code)
	{
		if(dialogWindow != null)
		{
			dialogWindow.handleKeyReleased(character, code);
			return;
		}
		content.handleKeyReleased(character, code);
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
	
	void handleMouseClick(Laptop gui, int x, int y, int mouseX, int mouseY, int mouseButton)
	{
		if(btnClose.isMouseOver())
		{
			if(content instanceof Application)
			{
				gui.close((Application) content);
				return;
			}

			if(parent != null)
			{
				parent.closeDialog();
			}
		}

		if(dialogWindow != null)
		{
			dialogWindow.handleMouseClick(gui, x, y, mouseX, mouseY, mouseButton);
			return;
		}

		content.handleMouseClick(mouseX, mouseY, mouseButton);
	}
	
	void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
	{
		if(dialogWindow != null)
		{
			dialogWindow.handleMouseDrag(mouseX, mouseY, mouseButton);
			return;
		}
		content.handleMouseDrag(mouseX, mouseY, mouseButton);
	}
	
	void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
	{
		if(dialogWindow != null)
		{
			dialogWindow.handleMouseRelease(mouseX, mouseY, mouseButton);
			return;
		}
		content.handleMouseRelease(mouseX, mouseY, mouseButton);
	}
	
	void handleMouseScroll(int mouseX, int mouseY, boolean direction)
	{
		if(dialogWindow != null)
		{
			dialogWindow.handleMouseScroll(mouseX, mouseY, direction);
			return;
		}
		content.handleMouseScroll(mouseX, mouseY, direction);
	}
	
	public void handleClose()
	{
		content.onClose();
	}
	
	private void updateComponents(int x, int y)
	{
		content.updateComponents(x + offsetX + 1, y + offsetY + 13);
		btnClose.x = x + offsetX + width - 12;
		btnClose.y = y + offsetY + 1;
	}
	
	public void openDialog(Dialog dialog)
	{
		if(dialogWindow != null)
		{
			dialogWindow.openDialog(dialog);
		}
		else
		{
			dialogWindow = new Window(dialog, null);
			dialogWindow.init(0, 0);
			dialogWindow.setParent(this);
		}
	}

	public void closeDialog()
	{
		if(dialogWindow != null)
		{
			dialogWindow.handleClose();
			dialogWindow = null;
		}
	}

	public Window<Dialog> getDialogWindow()
	{
		return dialogWindow;
	}

	public void close()
	{
		if(content instanceof Application)
		{
			laptop.close((Application) content);
			return;
		}
		if(parent != null)
		{
			parent.closeDialog();
		}
	}

	public void setParent(Window parent)
	{
		this.parent = parent;
	}

	public Window getParent()
	{
		return parent;
	}

	public T getContent()
	{
		return content;
	}
}
