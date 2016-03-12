package com.mrcrayfish.device.gui;

import com.mrcrayfish.device.app.components.ApplicationBar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonArrow extends GuiButton
{
	private Type type;
	
	public GuiButtonArrow(int buttonId, int x, int y, Type type) 
	{
		super(buttonId, x, y, 12, 12, "");
		this.type = type;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(ApplicationBar.APP_BAR_GUI);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);

			int state = this.getHoverState(this.hovered);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, state * this.width + type.i * 36, 18, this.width, this.height);
		}
	}
	
	public static enum Type 
	{
		LEFT(0), RIGHT(1);
		
		public int i;
		Type(int i) 
		{
			this.i = i;
		}
	}
}
