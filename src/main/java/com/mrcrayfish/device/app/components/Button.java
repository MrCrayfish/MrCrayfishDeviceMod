package com.mrcrayfish.device.app.components;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Layout;
import com.mrcrayfish.device.app.listener.ClickListener;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Button extends Component
{
	private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
	
	private String text;
	public boolean hovered;
	public int width, height;
	private ClickListener clickListener = null;
	
	public Button(String text, int x, int y, int left, int top, int width, int height) 
	{
		super(x, y, left, top);
		this.text = text;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(Minecraft mc, int mouseX, int mouseY, boolean windowActive) 
	{
		if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height && windowActive;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            int j = 14737632;

            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.text, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
        }
	}

	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton) 
	{
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + height))
		{
			if(clickListener != null)
			{
				clickListener.onClick(this, mouseButton);
			}
			playClickSound(Minecraft.getMinecraft().getSoundHandler());
		}
	}
	
	public void setClickListener(ClickListener clickListener) 
	{
		this.clickListener = clickListener;
	}
	
	protected int getHoverState(boolean mouseOver)
    {
        int i = 1;

        if (!this.enabled)
        {
            i = 0;
        }
        else if (mouseOver)
        {
            i = 2;
        }

        return i;
    }
	
	public void playClickSound(SoundHandler handler) 
	{
		handler.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
	}
	
	public boolean isInside(int mouseX, int mouseY)
	{
		return mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}

}
