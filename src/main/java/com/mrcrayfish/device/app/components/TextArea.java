package com.mrcrayfish.device.app.components;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Layout;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;

public class TextArea extends Component
{
	private FontRenderer fontRendererObj;
	private String text = "";
	private int width, height;
	private int maxLines;
	private int padding = 2;
	private int updateCount = 0;
	private boolean isFocused = false;
	
	/* Personalisation */
	private int textColour = Color.WHITE.getRGB();
	private int backgroundColour = Color.DARK_GRAY.getRGB();
	private int borderColour = Color.BLACK.getRGB();

	public TextArea(FontRenderer fontRendererObj, int x, int y, int left, int top, int width, int height) 
	{
		super(x, y, left, top);
		this.fontRendererObj = fontRendererObj;
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
	public void render(Minecraft mc, int mouseX, int mouseY, boolean windowActive)
	{
		if (this.visible)
        {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, borderColour);
			this.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, backgroundColour);
			String text = this.text;
			if (this.updateCount / 6 % 2 == 0)
	        {
				text = text + (this.isFocused ? "_" : "");
	        }
	        else
	        {
	        	text = text + EnumChatFormatting.GRAY + (this.isFocused ? "_" : "");
	        }
			this.fontRendererObj.drawSplitString(text, xPosition + padding + 1, yPosition + padding + 2, width - padding * 2 - 2, textColour);
        }
	}
	
	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton)
	{
		if(!this.visible || !this.enabled)
			return;
		
		boolean within = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
		this.isFocused = within;
	}
	
	@Override
	public void handleKeyTyped(char character, int code)
	{
		if(!isFocused)
		{
			return;
		}
		if (GuiScreen.isKeyComboCtrlV(code))
        {
            this.writeText(GuiScreen.getClipboardString());
        }
        else
        {
            switch (code)
            {
                case 14:
                	System.out.println("Test");
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

	public void writeText(String append)
	{
		String newText = this.text + append;
        if(Minecraft.getMinecraft().getRenderManager().getFontRenderer().listFormattedStringToWidth(newText + "_", width - padding * 2).size() <= maxLines)
		{
			this.text = newText;
		}
	}
	
	public void clear()
	{
		this.text = "";
	}
	
	public void setText(String text) 
	{
		this.text = text;
	}
	
	public String getText() 
	{
		return text;
	}
	
	public void setFocused(boolean isFocused) 
	{
		this.isFocused = isFocused;
	}
	
	public void setPadding(int padding) 
	{
		this.padding = padding;
		this.maxLines = (int) Math.floor((height - padding * 2) / fontRendererObj.FONT_HEIGHT);
	}
	
	public void setTextColour(Color color) 
	{
		this.textColour = color.getRGB();
	}
	
	public void setBackgroundColour(Color color) 
	{
		this.backgroundColour = color.getRGB();
	}
	
	public void setBorderColour(Color color) 
	{
		this.borderColour = color.getRGB();
	}

}
