package com.mrcrayfish.device.app.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Layout;
import com.mrcrayfish.device.app.listener.ClickListener;
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
	private ClickListener clickListener = null;
	
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
	public void init(Layout layout)
	{
		btnUp = new ButtonArrow(xPosition - left, yPosition - top, left + width + 3, top, ButtonArrow.Type.UP);
		btnUp.enabled = false;
		btnUp.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				if(offset > 0) {
					offset--;
					btnDown.enabled = true;
				}
				if(offset == 0) {
					btnUp.enabled = false;
				}
			}
		});
		layout.addComponent(btnUp);
		
		btnDown = new ButtonArrow(xPosition - left, yPosition - top, left + width + 3, top + 14, ButtonArrow.Type.DOWN);
		btnDown.setClickListener(new ClickListener() {
			@Override
			public void onClick(Component c, int mouseButton) {
				if(visibleItems + offset < items.size()) {
					offset++;
					btnUp.enabled = true;
				}
				if(visibleItems + offset == items.size()) {
					btnDown.enabled = false;
				}
			}
		});
		layout.addComponent(btnDown);
	}
	
	@Override
	public void render(Minecraft mc, int mouseX, int mouseY, boolean windowActive)
	{
		if (this.visible)
        {
			int height = 13;
			if(renderer != null)
			{
				height = renderer.getHeight();
			}
			drawHorizontalLine(xPosition, xPosition + width, yPosition, borderColour);
			drawVerticalLine(xPosition, yPosition, yPosition + (visibleItems * height) + visibleItems, borderColour);
			drawVerticalLine(xPosition + width, yPosition, yPosition + (visibleItems * height) + visibleItems, borderColour);
			drawHorizontalLine(xPosition, xPosition + width, yPosition + (visibleItems * height) + visibleItems, borderColour);
			for(int i = 0; i < visibleItems; i++)
			{
				E item = getItem(i);
				if(item != null)
				{
					if(renderer != null)
					{
						renderer.render(item, this, mc, xPosition + 1, yPosition + (i * (renderer.getHeight())) + 1 + i, width - 1, (i + offset) == selected);
						drawHorizontalLine(xPosition + 1, xPosition + width - 1, yPosition + (i * height) + i + height + 1, borderColour);
					}
					else
					{
						drawRect(xPosition + 1, yPosition + (i * 14) + 1, xPosition + width, yPosition + 13 + (i * 14) + 1, (i + offset) != selected ? backgroundColour : Color.DARK_GRAY.getRGB());
						drawString(mc.fontRendererObj, item.toString(), xPosition + 3, yPosition + 3 + (i * 14), textColour);
						drawHorizontalLine(xPosition + 1, xPosition + width - 1, yPosition + (i * height) + i, borderColour);
					}
				}
			}
        }
	}

	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton)
	{
		if(!this.visible || !this.enabled)
			return;
		
		int height = 13;
		if(renderer != null) height = renderer.getHeight();
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + visibleItems * height + visibleItems))
		{
			for(int i = 0; i < visibleItems && i < items.size(); i++)
			{
				if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition + (i * height) + i, xPosition + width, yPosition + (i * height) + i + height))
				{
					this.selected = i + offset;
					if(clickListener != null)
					{
						clickListener.onClick(this, mouseButton);
					}
				}
			}
		}
	}

	public void setListItemRenderer(ListItemRenderer<E> renderer)
	{
		this.renderer = renderer;
	}
	
	public void setClickListener(ClickListener clickListener) 
	{
		this.clickListener = clickListener;
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
	
	public E getItem(int pos)
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
	
	public List<E> getItems()
	{
		return items;
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
