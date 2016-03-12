package com.mrcrayfish.device.app.components;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.gui.GuiButtonClose;
import com.mrcrayfish.device.gui.GuiLaptop;
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

public class Window extends Component
{
	public static final ResourceLocation WINDOW_GUI = new ResourceLocation("cdm:textures/gui/application.png");
	
	private Application app;
	public int width, height;
	public int offsetX, offsetY;
	
	private GuiButton btnClose;
	
	public Window(Application app) 
	{
		this.app = app;
		this.setWidth(app.getWidth());
		this.setHeight(app.getHeight());
	}
	
	private void setWidth(int width) 
	{
		this.width = width + 2;
		if(this.width > 236)
		{
			this.width = 236;
		}
	}
	
	private void setHeight(int height) 
	{
		this.height = height + 14;
		if(this.height > 120)
		{
			this.height = 120;
		}
	}

	@Override
	public void init(List<GuiButton> buttons, int x, int y)
	{
		btnClose = new GuiButtonClose(0, x + offsetX + width - 12, y + offsetY + 1);
		buttons.add(btnClose);
		
		app.init(buttons, x + offsetX + 1, y + offsetY + 13);
	}
	
	public void onTick() 
	{
		app.onTick();
	}
	
	@Override
	public void render(GuiLaptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY)
	{
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
		
		mc.fontRendererObj.drawString(app.getDisplayName(), x + offsetX + 3, y + offsetY + 3, Color.WHITE.getRGB(), true);
		
		GlStateManager.disableBlend();

		app.render(gui, mc, x + offsetX + 1, y + offsetY + 13);
		
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public void handleButtonClick(GuiLaptop laptop, GuiButton button) 
	{
		if(button.equals(btnClose))
		{
			laptop.closeApplication();
		}
		else
		{
			app.handleButtonClick(button);
		}
	}
	
	@Override
	public void handleClick(GuiLaptop gui, int x, int y, int mouseX, int mouseY, int mouseButton)
	{
		app.handleClick(gui, mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void handleKeyTyped(char character, int code)
	{
		app.handleKeyTyped(character, code);
	}
	
	@Override
	public void handleDrag(GuiScreen gui, int x, int y, int mouseDX, int mouseDY, int screenStartX, int screenStartY)
	{
		int newX = x + offsetX + mouseDX;
		int newY = y + offsetY + mouseDY;
		
		if(newX <= screenStartX)
		{
			this.offsetX = screenStartX - x;
		}
		else if(newX + width > screenStartX + 364)
		{
			this.offsetX = x - screenStartX;
		}
		else
		{
			this.offsetX += mouseDX;
		}
		
		if(newY < screenStartY)
		{
			this.offsetY = screenStartY - y;
		}
		else if(newY + height > screenStartY + 178)
		{
			this.offsetY = y - screenStartY;
		}
		else
		{
			this.offsetY += mouseDY;
		}
		
		updateComponents(x, y);
		app.updateButtons(x + offsetX + 1, y + offsetY + 13);
	}
	
	@Override
	public void handleClose(List<GuiButton> buttons)
	{
		buttons.remove(btnClose);
		app.hideButtons(buttons);
	}
	
	@Override
	public void updateComponents(int x, int y)
	{
		btnClose.xPosition = x + offsetX + width - 12;
		btnClose.yPosition = y + offsetY + 1;
	}
	
	@Override
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
