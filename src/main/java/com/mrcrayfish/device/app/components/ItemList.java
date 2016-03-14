package com.mrcrayfish.device.app.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;

public class ItemList<E> extends Component
{
	private int width;
	private int visibleItems;
	private int offset;
	private int selected = -1;
	
	private List<E> items = new ArrayList<E>();
	private ListItemRenderer<E> renderer = null;
	
	private Button btnUp;
	private Button btnDown;
	
	private int textColour = Color.WHITE.getRGB();
	private int backgroundColour = Color.GRAY.getRGB();
	private int borderColour = Color.BLACK.getRGB();
	
	public ItemList(int x, int y, int left, int top, int width, int visibleItems) 
	{
		super(x, y, left, top);
		this.width = width;
		this.visibleItems = visibleItems;
	}
	
	@Override
	public void init(Application app)
	{
		btnUp = new ButtonArrow(xPosition, yPosition, width + 2, -1, ButtonArrow.Type.UP);
		btnUp.enabled = false;
		app.addComponent(btnUp);
		
		btnDown = new ButtonArrow(xPosition, yPosition, width + 2, 12, ButtonArrow.Type.DOWN);
		app.addComponent(btnDown);
	}
	
	@Override
	public void render(Minecraft mc, int mouseX, int mouseY)
	{
		drawRect(xPosition - 1, yPosition - 1, xPosition + width, yPosition, borderColour);
		drawRect(xPosition - 1, yPosition - 1, xPosition, yPosition + visibleItems * renderer.getHeight(), borderColour);
		drawRect(xPosition + width, yPosition - 1, xPosition + width + 1, yPosition + visibleItems * renderer.getHeight(), borderColour);
		for(int i = 0; i < visibleItems; i++)
		{
			E item = getItem(i);
			if(item != null)
			{
				if(renderer != null)
				{
					renderer.render(item, this, mc, xPosition, yPosition + (i * (renderer.getHeight())), width, (i + offset) == selected);
					drawRect(xPosition - 1, yPosition + (i + 1) * renderer.getHeight() - 1, xPosition + width, yPosition + (i + 1) * renderer.getHeight(), borderColour);
				}
				else
				{
					drawRect(xPosition, yPosition + (i * 14), xPosition + width, yPosition + 13 + (i * 14), backgroundColour);
					drawString(mc.fontRendererObj, item.toString(), xPosition + 3, yPosition + 3 + (i * 14), textColour);
				}
			}
		}
	}

	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton)
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
	
	@Override
	public void handleButtonClick(Button button)
	{
		System.out.println("Test");
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
