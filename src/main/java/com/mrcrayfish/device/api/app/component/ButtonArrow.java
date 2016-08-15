package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ButtonArrow extends Button
{
	protected Type type;
	
	public ButtonArrow(int x, int y, int left, int top, Type type) 
	{
		super("", x, y, left, top, 12, 12);
		this.type = type;
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(Component.COMPONENTS_GUI);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height && windowActive;

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);

			int state = this.getHoverState(this.hovered);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, state * this.width + type.i * 36, 0, this.width, this.height);
		}
	}
	
	public static enum Type 
	{
		LEFT(0), UP(1), RIGHT(2), DOWN(3);
		
		public int i;
		Type(int i) 
		{
			this.i = i;
		}
	}
}
