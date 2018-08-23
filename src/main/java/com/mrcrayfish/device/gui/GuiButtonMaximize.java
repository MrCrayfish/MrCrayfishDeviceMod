package com.mrcrayfish.device.gui;

import com.mrcrayfish.device.core.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonMaximize extends GuiButtonWindow
{
	private boolean maximized;
	
	public GuiButtonMaximize(int buttonId, int x, int y) 
	{
		super(buttonId, x, y);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(Window.WINDOW_GUI);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			
			int state = this.getHoverState(this.hovered);
			this.drawTexturedModalRect(this.x, this.y, state * this.width + 26, (2 - this.id + (this.maximized ? 1 : 0)) * this.height, this.width, this.height);
		}
	}
	
	public void setMaximized(boolean maximized)
	{
		this.maximized = maximized;
	}
}
