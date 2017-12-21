package com.mrcrayfish.device.gui;

import com.mrcrayfish.device.core.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonClose extends GuiButton {
	public GuiButtonClose(int buttonId, int x, int y) {
		super(buttonId, x, y, 11, 11, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			mc.getTextureManager().bindTexture(Window.WINDOW_GUI);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = (mouseX >= x) && (mouseY >= y) && (mouseX < (x + width)) && (mouseY < (y + height));

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);

			int state = getHoverState(hovered);
			this.drawTexturedModalRect(x, y, (state * width) + 15, 0, width, height);
		}
	}
}
