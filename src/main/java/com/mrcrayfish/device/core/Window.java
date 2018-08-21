package com.mrcrayfish.device.core;

import java.awt.Color;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.gui.GuiButtonWindow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class Window<T extends Wrappable>
{
	public static final ResourceLocation WINDOW_GUI = new ResourceLocation("cdm:textures/gui/application.png");

	public static final int Color_WINDOW_DARK = new Color(0F, 0F, 0F, 0.25F).getRGB();

	protected T content;
	private int width, height;
	private int offsetX, offsetY;
	private int smallOffsetX, smallOffsetY;
	private int smallWidth, smallHeight;
	private boolean maximized;

	final Laptop laptop;
	Window<Dialog> dialogWindow = null;
	Window<? extends Wrappable> parent = null;

	protected GuiButton btnMaximize;
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
		if (this.width > Laptop.SCREEN_WIDTH - offsetX)
		{
			this.width = Laptop.SCREEN_WIDTH - offsetX;
		}
	}

	void setHeight(int height)
	{
		this.height = height + 14;
		if (this.height > 178 - offsetY)
		{
			this.height = 178 - offsetY;
		}
	}

	void init(int x, int y, @Nullable NBTTagCompound intent)
	{
		btnMaximize = new GuiButtonWindow(1, x + offsetX + width - 12 * 2 + 1, y + offsetY + 1);
		btnClose = new GuiButtonWindow(2, x + offsetX + width - 12, y + offsetY + 1);
		content.init(intent);
	}

	public void onTick()
	{
		if (dialogWindow != null)
		{
			dialogWindow.onTick();
		}
		content.onTick();

		btnMaximize.enabled = content.isResizable();
		btnMaximize.visible = content.isDecorated();
		btnClose.visible = content.isDecorated();
	}

	public void render(Laptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
	{
		if (content.isPendingLayoutUpdate())
		{
			this.setWidth(content.getWidth());
			this.setHeight(content.getHeight());
			this.offsetX = (Laptop.SCREEN_WIDTH - width) / 2;
			this.offsetY = (Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height) / 2;
			updateComponents(x, y);
			content.clearPendingLayout();
		}

		int width = this.getWidth();
		int height = this.getHeight();
		int offsetX = this.getOffsetX();
		int offsetY = this.getOffsetY();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(WINDOW_GUI);

		if (content.isDecorated())
		{
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
		}

		/* Center */
		RenderUtil.drawRectWithTexture(x + offsetX + 1, y + offsetY + 13, 1, 13, width - 2, height - 14, 13, 1);

		if (content.isDecorated())
		{
			String windowTitle = content.getWindowTitle();
			if (mc.fontRenderer.getStringWidth(windowTitle) > width - 2 - 13 - 3) // window width, border, close button, padding, padding
			{
				windowTitle = mc.fontRenderer.trimStringToWidth(windowTitle, width - 2 - 13 - 3);
			}
			mc.fontRenderer.drawString(windowTitle, x + offsetX + 3, y + offsetY + 3, Color.WHITE.getRGB(), true);
		}

		btnMaximize.drawButton(mc, mouseX, mouseY, partialTicks);
		btnClose.drawButton(mc, mouseX, mouseY, partialTicks);

		GlStateManager.disableBlend();

		/* Render content */
		content.render(gui, mc, x + offsetX + 1, y + offsetY + 13, mouseX, mouseY, active && dialogWindow == null, partialTicks);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (dialogWindow != null)
		{
			if (content.isDecorated())
			{
				Gui.drawRect(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height, Color_WINDOW_DARK);
			} else
			{
				Gui.drawRect(x + offsetX + 1, y + offsetY + 13, x + offsetX + width - 1, y + offsetY + height - 1, Color_WINDOW_DARK);
			}
			dialogWindow.render(gui, mc, x, y, mouseX, mouseY, active, partialTicks);
		}
	}

	public void handleKeyTyped(char character, int code)
	{
		if (dialogWindow != null)
		{
			dialogWindow.handleKeyTyped(character, code);
			return;
		}
		content.handleKeyTyped(character, code);
	}

	public void handleKeyReleased(char character, int code)
	{
		if (dialogWindow != null)
		{
			dialogWindow.handleKeyReleased(character, code);
			return;
		}
		content.handleKeyReleased(character, code);
	}

	public void handleWindowMove(int screenStartX, int screenStartY, int mouseDX, int mouseDY)
	{
		setPosition(offsetX + mouseDX, offsetY + mouseDY);
		updateComponents(screenStartX, screenStartY);
	}

	public boolean resize(int screenStartX, int screenStartY, int width, int height)
	{
		boolean result = content.resize(width, height);
		content.onResize(content.getWidth(), content.getHeight());
		setWidth(content.getWidth());
		setHeight(content.getHeight());
		updateComponents(screenStartX, screenStartY);
		return result;
	}
	
	private void setPosition(int newX, int newY) {
		if (newX >= 0 && newX <= Laptop.SCREEN_WIDTH - width)
		{
			this.offsetX = newX;
		} else if (newX < 0)
		{
			this.offsetX = 0;
		} else
		{
			this.offsetX = Laptop.SCREEN_WIDTH - width;
		}

		if (newY >= 0 && newY <= Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height)
		{
			this.offsetY = newY;
		} else if (newY < 0)
		{
			this.offsetY = 0;
		} else
		{
			this.offsetY = Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height;
		}
	}

	void handleMouseClick(Laptop gui, int x, int y, int mouseX, int mouseY, int mouseButton)
	{
		int posX = (gui.width - Laptop.SCREEN_WIDTH) / 2;
		int posY = (gui.height - Laptop.SCREEN_HEIGHT) / 2;

		if (btnMaximize.isMouseOver())
		{
			if (content.isResizable())
			{
				if (!maximized)
				{
					smallOffsetX = offsetX;
					smallOffsetY = offsetY;
					smallWidth = width;
					smallHeight = height;
					offsetX = 0;
					offsetY = 0;
					width = Laptop.SCREEN_WIDTH;
					height = Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT;
				} else
				{
					width = smallWidth - 2;
					height = smallHeight - 14;
					offsetX = smallOffsetX;
					offsetY = smallOffsetY;
				}
				maximized = !maximized;
				
				this.resize(posX, posY, width, height);
				this.updateComponents(posX, posY);
			}
		}

		if (btnClose.isMouseOver())
		{
			if (content instanceof Application)
			{
				gui.closeApplication(((Application) content).getInfo());
				return;
			}

			if (parent != null)
			{
				parent.closeDialog();
			}
		}

		if (dialogWindow != null)
		{
			dialogWindow.handleMouseClick(gui, x, y, mouseX, mouseY, mouseButton);
			return;
		}

		content.handleMouseClick(mouseX, mouseY, mouseButton);
	}

	void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
	{
		if (dialogWindow != null)
		{
			dialogWindow.handleMouseDrag(mouseX, mouseY, mouseButton);
			return;
		}
		content.handleMouseDrag(mouseX, mouseY, mouseButton);
	}

	void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
	{
		if (dialogWindow != null)
		{
			dialogWindow.handleMouseRelease(mouseX, mouseY, mouseButton);
			return;
		}
		content.handleMouseRelease(mouseX, mouseY, mouseButton);
	}

	void handleMouseScroll(int mouseX, int mouseY, boolean direction)
	{
		if (dialogWindow != null)
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
		int width = this.getWidth();
		int height = this.getHeight();
		int offsetX = this.getOffsetX();
		int offsetY = this.getOffsetY();

		content.updateComponents(x + offsetX + 1, y + offsetY + 13);
		btnMaximize.x = x + offsetX + width - 12 * 2 + 1;
		btnMaximize.y = y + offsetY + 1;
		btnClose.x = x + offsetX + width - 12;
		btnClose.y = y + offsetY + 1;
	}

	public void openDialog(Dialog dialog)
	{
		if (dialogWindow != null)
		{
			dialogWindow.openDialog(dialog);
		} else
		{
			dialogWindow = new Window(dialog, null);
			dialogWindow.init(0, 0, null);
			dialogWindow.setParent(this);
		}
	}

	public void closeDialog()
	{
		if (dialogWindow != null)
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
		if (content instanceof Application)
		{
			laptop.closeApplication(((Application) content).getInfo());
			return;
		}
		if (parent != null)
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

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}

	public int getOffsetX()
	{
		return this.offsetX;
	}

	public int getOffsetY()
	{
		return this.offsetY;
	}
}
