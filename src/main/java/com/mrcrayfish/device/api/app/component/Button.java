package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icon;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;

public class Button extends Component
{
	protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");

	protected String text;
	protected String toolTip, toolTipTitle;
	protected boolean hovered;

	protected int padding = 5;
	protected int width, height;
	protected boolean explicitSize = false;

	protected ResourceLocation iconResource;
	protected int iconU, iconV;
	protected int iconWidth, iconHeight;
	protected int iconSourceWidth;
	protected int iconSourceHeight;

	protected ClickListener clickListener = null;

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param text text to be displayed in the button
	 */
	public Button(int left, int top, String text)
	{
		super(left, top);
		this.width = getTextWidth(text) + padding * 2;
		this.height = 16;
		this.text = text;
	}

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param text text to be displayed in the button
	 */
	public Button(int left, int top, int buttonWidth, int buttonHeight, String text)
	{
		super(left, top);
		this.explicitSize = true;
		this.width = buttonWidth;
		this.height = buttonHeight;
		this.text = text;
	}

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param icon
	 */
	public Button(int left, int top, Icon icon)
	{
		super(left, top);
		this.padding = 3;
		this.width = Icon.ICON_SIZE + padding * 2;
		this.height = Icon.ICON_SIZE + padding * 2;
		this.setIcon(icon);
	}

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param icon
	 */
	public Button(int left, int top, int buttonWidth, int buttonHeight, Icon icon)
	{
		super(left, top);
		this.explicitSize = true;
		this.width = buttonWidth;
		this.height = buttonHeight;
		this.setIcon(icon);
	}

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param icon
	 */
	public Button(int left, int top, String text, Icon icon)
	{
		this(left, top, text);
		this.setIcon(icon);
	}

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param icon
	 */
	public Button(int left, int top, int buttonWidth, int buttonHeight, String text, Icon icon)
	{
		super(left, top);
		this.text = text;
		this.explicitSize = true;
		this.width = buttonWidth;
		this.height = buttonHeight;
		this.setIcon(icon);
	}

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 */
	public Button(int left, int top, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight)
	{
		super(left, top);
		this.padding = 3;
		this.setIcon(iconResource, iconU, iconV, iconWidth, iconHeight);
	}

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 */
	public Button(int left, int top, int buttonWidth, int buttonHeight, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight)
	{
		super(left, top);
		this.explicitSize = true;
		this.width = buttonWidth;
		this.height = buttonHeight;
		this.setIcon(iconResource, iconU, iconV, iconWidth, iconHeight);
	}

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 */
	public Button(int left, int top, String text, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight)
	{
		super(left, top);
		this.text = text;
		this.setIcon(iconResource, iconU, iconV, iconWidth, iconHeight);
	}

	/**
	 * Alternate button constructor
	 *
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 */
	public Button(int left, int top, int buttonWidth, int buttonHeight, String text, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight)
	{
		super(left, top);
		this.text = text;
		this.explicitSize = true;
		this.width = buttonWidth;
		this.height = buttonHeight;
		this.setIcon(iconResource, iconU, iconV, iconWidth, iconHeight);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
            mc.getTextureManager().bindTexture(Component.COMPONENTS_GUI);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = isInside(mouseX, mouseY) && windowActive;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            
            /* Corners */
            RenderUtil.drawRectWithTexture(xPosition, yPosition, 96 + i * 5, 12, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(xPosition + width - 2, yPosition, 99 + i * 5, 12, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(xPosition + width - 2, yPosition + height - 2, 99 + i * 5, 15, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(xPosition, yPosition + height - 2, 96 + i * 5, 15, 2, 2, 2, 2);

            /* Middles */
            RenderUtil.drawRectWithTexture(xPosition + 2, yPosition, 98 + i * 5, 12, width - 4, 2, 1, 2);
            RenderUtil.drawRectWithTexture(xPosition + width - 2, yPosition + 2, 99 + i * 5, 14, 2, height - 4, 2, 1);
            RenderUtil.drawRectWithTexture(xPosition + 2, yPosition + height - 2, 98 + i * 5, 15, width - 4, 2, 1, 2);
            RenderUtil.drawRectWithTexture(xPosition, yPosition + 2, 96 + i * 5, 14, 2, height - 4, 2, 1);
            
            /* Center */
            RenderUtil.drawRectWithTexture(xPosition + 2, yPosition + 2, 98 + i * 5, 14, width - 4, height - 4, 1, 1);
            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int contentWidth = (iconResource != null ? iconWidth: 0) + getTextWidth(text);
            if(iconResource != null && text != null) contentWidth += 3;
            int contentX = (int) Math.ceil((width - contentWidth) / 2.0);

            if(iconResource != null)
			{
				int iconY = (height - iconHeight) / 2;
				mc.getTextureManager().bindTexture(iconResource);
				RenderUtil.drawRectWithTexture(x + contentX, y + iconY, iconU, iconV, iconWidth, iconHeight, iconWidth, iconHeight, iconSourceWidth, iconSourceHeight);
			}

			if(text != null)
			{
				int textY = (height - mc.fontRendererObj.FONT_HEIGHT) / 2 + 1;
				int textOffsetX = iconResource != null ? iconWidth + 3 : 0;
				int textColour = !Button.this.enabled ? 10526880 : (Button.this.hovered ? 16777120 : 14737632);
				drawString(mc.fontRendererObj, text, x + contentX + textOffsetX, y + textY, textColour);
			}
        }
	}
	
	@Override
	public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) 
	{
        if(this.hovered && this.toolTip != null)
        {
        	laptop.drawHoveringText(Arrays.asList(TextFormatting.GOLD + this.toolTipTitle, this.toolTip), mouseX, mouseY);
        }
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) 
	{
		if(!this.visible || !this.enabled)
			return;
		
		if(this.hovered) 
		{
			if(clickListener != null)
			{
				clickListener.onClick(this, mouseButton);
			}
			playClickSound(Minecraft.getMinecraft().getSoundHandler());
		}
	}
	
	/**
	 * Sets the click listener. Use this to handle custom actions
	 * when you press the button.
	 * 
	 * @param clickListener the click listener
	 */
	public final void setClickListener(ClickListener clickListener) 
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
	
	protected void playClickSound(SoundHandler handler)
	{
		handler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
	
	protected boolean isInside(int mouseX, int mouseY)
	{
		return mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}

	public void setSize(int width, int height)
	{
		this.explicitSize = true;
		this.width = width;
		this.height = height;
	}

	public void setPadding(int padding)
	{
		this.padding = padding;
		updateSize();
	}

	/**
	 * Sets the text to display in the button
	 * 
	 * @param text the text
	 */
	public void setText(String text)
	{
		this.text = text;
		updateSize();
	}
	
	/**
	 * Gets the text currently displayed in the button
	 * 
	 * @return the button text
	 */
	public String getText()
	{
		return text;
	}

	public void setIcon(ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight)
	{
		this.iconU = iconU;
		this.iconV = iconV;
		this.iconResource = iconResource;
		this.iconWidth = iconWidth;
		this.iconHeight = iconHeight;
		this.iconSourceWidth = 256;
		this.iconSourceHeight = 256;
		updateSize();
	}

	public void setIcon(Icon icon)
	{
		this.iconU = icon.getU();
		this.iconV = icon.getV();
		this.iconResource = Icon.ICON_ASSET;
		this.iconWidth = Icon.ICON_SIZE;
		this.iconHeight = Icon.ICON_SIZE;
		this.iconSourceWidth = Icon.GRID_SIZE * Icon.ICON_SIZE;
		this.iconSourceHeight = Icon.GRID_SIZE * Icon.ICON_SIZE;
		updateSize();
	}

	public void removeIcon()
	{
		this.iconResource = null;
		updateSize();
	}

	private void updateSize()
	{
		if(explicitSize) return;
		int height = Math.max(iconHeight + padding * 2, 16);
		int width = padding * 2;

		if(iconResource != null)
		{
			width += iconWidth;
		}

		if(text != null)
		{
			width += getTextWidth(text);
		}

		if(iconResource != null && text != null)
		{
			width += 3;
		}

		this.width = width;
		this.height = height;
	}

	/**
	 * Displays a message when hovering the button.
	 * 
	 * @param toolTipTitle title of the tool tip
	 * @param toolTip description of the tool tip
	 */
	public void setToolTip(String toolTipTitle, String toolTip)
	{
		this.toolTipTitle = toolTipTitle;
		this.toolTip = toolTip;
	}

	private static int getTextWidth(String text)
	{
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
		boolean flag = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(false);
		int width = fontRenderer.getStringWidth(text);
		fontRenderer.setUnicodeFlag(flag);
		return width;
	}
	
	//TODO add button text colour and button colour
}
