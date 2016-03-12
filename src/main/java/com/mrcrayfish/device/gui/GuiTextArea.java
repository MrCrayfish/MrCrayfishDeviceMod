package com.mrcrayfish.device.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;

public class GuiTextArea extends Gui
{
	private FontRenderer fontRendererObj;
	private String text = "";
	public int xPosition, yPosition;
	private int width, height;
	private int maxLines;
	private int padding = 2;
	private int updateCount = 0;
	private boolean isFocused = false;
	
	//Personalisation
	private int textColour = Color.WHITE.getRGB();
	private int backgroundColour = Color.DARK_GRAY.getRGB();
	private int borderColour = Color.BLACK.getRGB();

	public GuiTextArea(FontRenderer fontRendererObj, int x, int y, int width, int height) 
	{
		this.fontRendererObj = fontRendererObj;
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.height = height;
		this.maxLines = (int) Math.floor((height - padding * 2) / fontRendererObj.FONT_HEIGHT);
	}
	
	public void onTick()
	{
		updateCount++;
	}
	
	public void draw()
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, backgroundColour);
		this.drawHorizontalLine(xPosition - 1, xPosition + width, yPosition - 1, borderColour);
		this.drawHorizontalLine(xPosition - 1, xPosition + width, yPosition + height, borderColour);
		this.drawVerticalLine(xPosition - 1, yPosition - 1, yPosition + height, borderColour);
		this.drawVerticalLine(xPosition + width, yPosition - 1, yPosition + height, borderColour);
		String text = this.text;
		if (this.updateCount / 6 % 2 == 0)
        {
			text = text + "_";
        }
        else
        {
        	text = text + EnumChatFormatting.GRAY + "_";
        }
		this.fontRendererObj.drawSplitString(text, xPosition + padding, yPosition + padding, width - padding * 2, textColour);
	}
	
	public void onMouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		boolean within = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
		this.isFocused = within;
	}
	
	public void onKeyTyped(char character, int code)
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
	
	public void setText(String text) 
	{
		this.writeText(text);
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
