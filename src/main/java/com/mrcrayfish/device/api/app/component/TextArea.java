package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

public class TextArea extends Component
{
	protected FontRenderer fontRendererObj;
	
	protected String text = "";
	protected String placeholder = null;
	protected int width, height;
	protected int maxLines;
	protected int padding = 2;
	protected int updateCount = 0;
	protected boolean isFocused = false;
	protected boolean editable = true;
	
	/* Personalisation */
	protected int placeholderColour = new Color(1.0F, 1.0F, 1.0F, 0.35F).getRGB();
	protected int textColour = Color.WHITE.getRGB();
	protected int backgroundColour = Color.DARK_GRAY.getRGB();
	protected int borderColour = Color.BLACK.getRGB();

	/**
	 * Default text area constructor
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width the width of the text area
	 * @param height the height of the text area
	 */
	public TextArea(int left, int top, int width, int height) 
	{
		super(left, top);
		this.fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
		this.width = width;
		this.height = height;
		this.maxLines = (int) Math.floor((height - padding * 2) / fontRendererObj.FONT_HEIGHT);
	}
	
	@Override
	public void handleTick()
	{
		updateCount++;
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, borderColour);
			Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, backgroundColour);

			if(!isFocused && placeholder != null && text.isEmpty())
			{
				GlStateManager.enableBlend();
				mc.fontRendererObj.drawSplitString(placeholder, x + padding + 1, y + padding + 2, width - padding * 2 - 2, placeholderColour);
			}

			String text = this.text;
			if (this.updateCount / 6 % 2 == 0)
	        {
				text = text + (this.isFocused ? "_" : "");
	        }
	        else
	        {
	        	text = text + TextFormatting.GRAY + (this.isFocused ? "_" : "");
	        }
			this.fontRendererObj.drawSplitString(text, xPosition + padding + 1, yPosition + padding + 2, width - padding * 2 - 2, textColour);
        }
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(!this.visible || !this.enabled || !this.editable)
			return;

		this.isFocused = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
	}
	
	@Override
	public void handleKeyTyped(char character, int code)
	{
		if(!this.visible || !this.enabled || !this.isFocused || !this.editable)
			return;

		if (GuiScreen.isKeyComboCtrlV(code))
        {
            this.writeText(GuiScreen.getClipboardString());
        }
        else
        {
            switch (code)
            {
                case 14:
                    if (text.length() > 0)
                    {
                        text = text.substring(0, text.length() - 1);
                    }
                    return;
                case 28:
                case 156:
                    this.writeText("\n");
                    return;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(character))
                    {
                        this.writeText(Character.toString(character));
                    }
            }
        }
	}
	
	/**
	 * Appends text to the text area
	 * 
	 * @param append the text to append
	 */
	public void writeText(String append)
	{
		String newText = this.text + append;
        if(Minecraft.getMinecraft().getRenderManager().getFontRenderer().listFormattedStringToWidth(newText + "_", width - padding * 2).size() <= maxLines)
		{
			this.text = newText;
		}
	}
	
	/**
	 * Clears the text
	 */
	public void clear()
	{
		this.text = "";
	}
	
	/**
	 * Sets the text for this component 
	 * 
	 * @param text the text
	 */
	public void setText(String text) 
	{
		this.text = text;
	}
	
	/**
	 * Gets the text in the box
	 * 
	 * @return the text
	 */
	public String getText() 
	{
		return text;
	}

	public void setPlaceholder(String placeholder)
	{
		this.placeholder = placeholder;
	}

	/**
	 * Sets this text area focused. Makes it available for typing.
	 * 
	 * @param isFocused whether the text area should be focused
	 */
	public void setFocused(boolean isFocused) 
	{
		this.isFocused = isFocused;
	}
	
	/**
	 * Sets the padding for the text area
	 * 
	 * @param padding the padding size
	 */
	public void setPadding(int padding) 
	{
		this.padding = padding;
		this.maxLines = (int) Math.floor((height - padding * 2) / fontRendererObj.FONT_HEIGHT);
	}
	
	/**
	 * Sets the text colour for this component
	 * 
	 * @param color the text colour
	 */
	public void setTextColour(Color color) 
	{
		this.textColour = color.getRGB();
	}
	
	/**
	 * Sets the background colour for this component
	 * 
	 * @param color the background colour
	 */
	public void setBackgroundColour(Color color) 
	{
		this.backgroundColour = color.getRGB();
	}
	
	/**
	 * Sets the border colour for this component
	 * 
	 * @param color the border colour
	 */
	public void setBorderColour(Color color) 
	{
		this.borderColour = color.getRGB();
	}
	
	/**
	 * Sets whether the user can edit the text
	 * 
	 * @param editable is this component editable
	 */
	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}

}
