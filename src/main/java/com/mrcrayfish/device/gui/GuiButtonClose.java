package com.mrcrayfish.device.gui;

import com.mrcrayfish.device.core.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonClose extends GuiButton
{
	public GuiButtonClose(int buttonId, int x, int y) 
	{
		super(buttonId, x, y, 11, 11, "");
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(Window.WINDOW_GUI);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);

			int state = this.getHoverState(this.hovered);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, state * this.width + 15, 0, this.width, this.height);
		}
	}
}
