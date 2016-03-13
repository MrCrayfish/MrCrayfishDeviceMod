package com.mrcrayfish.device.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.app.components.ListItemRenderer;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.ItemRenderer;

public class GuiList<E> extends Gui
{
	public int xPosition;
	public int yPosition;
	
	private int width;
	private int visibleItems;
	private int offset;
	private int selected = -1;
	
	private List<E> items = new ArrayList<E>();
	private ListItemRenderer<E> renderer = null;
	
	private GuiButton btnUp;
	private GuiButton btnDown;
	
	private int textColour = Color.WHITE.getRGB();
	private int backgroundColour = Color.GRAY.getRGB();
	private int borderColour = Color.BLACK.getRGB();
	
	public GuiList(int x, int y, int width, int visibleItems) 
	{
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.visibleItems = visibleItems;
	}
	
	public void init(List<GuiButton> buttons)
	{
		btnUp = new GuiButtonArrow(0, xPosition + width + 2, yPosition - 1, GuiButtonArrow.Type.RIGHT);
		btnUp.enabled = false;
		btnDown = new GuiButtonArrow(0, xPosition + width + 2, yPosition + 12, GuiButtonArrow.Type.LEFT);
		buttons.add(btnUp);
		buttons.add(btnDown);
	}
	
	public void render(Gui gui, Minecraft mc)
	{
		gui.drawRect(xPosition - 1, yPosition - 1, xPosition + width, yPosition, borderColour);
		gui.drawRect(xPosition - 1, yPosition - 1, xPosition, yPosition + visibleItems * renderer.getHeight(), borderColour);
		gui.drawRect(xPosition + width, yPosition - 1, xPosition + width + 1, yPosition + visibleItems * renderer.getHeight(), borderColour);
		for(int i = 0; i < visibleItems; i++)
		{
			E item = getItem(i);
			if(item != null)
			{
				if(renderer != null)
				{
					renderer.render(item, gui, mc, xPosition, yPosition + (i * (renderer.getHeight())), width, (i + offset) == selected);
					gui.drawRect(xPosition - 1, yPosition + (i + 1) * renderer.getHeight() - 1, xPosition + width, yPosition + (i + 1) * renderer.getHeight(), borderColour);
				}
				else
				{
					gui.drawRect(xPosition, yPosition + (i * 14), xPosition + width, yPosition + 13 + (i * 14), backgroundColour);
					gui.drawString(mc.fontRendererObj, item.toString(), xPosition + 3, yPosition + 3 + (i * 14), textColour);
				}
			}
		}
	}
	
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		int height = 10;
		if(renderer != null) height = renderer.getHeight();
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + visibleItems * height + visibleItems))
		{
			for(int i = 0; i < visibleItems && i < items.size(); i++)
			{
				if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition + (i * (renderer.getHeight())), xPosition + width, yPosition + height + (i * (renderer.getHeight()))))
				{
					this.selected = i + offset;
				}
			}
		}
	}
	
	public void handleButtonClick(GuiButton button)
	{
		if(items.size() > 3)
		{
			if(button == btnUp)
			{
				if(offset > 0)
				{
					offset--;
					btnDown.enabled = true;
				}
				if(offset == 0)
				{
					btnUp.enabled = false;
				}
			}
			if(button == btnDown)
			{
				if(visibleItems + offset < items.size())
				{
					offset++;
					btnUp.enabled = true;
				}
				if(visibleItems + offset == items.size())
				{
					btnDown.enabled = false;
				}
			}
		}
	}
	
	public void handleClose(List<GuiButton> buttons)
	{
		buttons.remove(btnUp);
		buttons.remove(btnDown);
	}
	
	public void setListItemRenderer(ListItemRenderer<E> renderer)
	{
		this.renderer = renderer;
	}
	
	public void addItem(E e)
	{
		if(e != null)
		{
			items.add(e);
		}
	}
	
	public void removeItem(int index)
	{
		if(index >= 0 && index < items.size())
		{
			items.remove(index);
			if(index == selected)
				selected = -1;
		}
	}
	
	private E getItem(int pos)
	{
		if(pos + offset < items.size())
		{
			return items.get(pos + offset);
		}
		return null;
	}
	
	public E getSelectedItem()
	{
		if(selected >= 0 && selected < items.size())
		{
			return items.get(selected);
		}
		return null;
	}
	
	public int getSelectedIndex()
	{
		return selected;
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
