package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemList<E> extends Component implements Iterable<E>
{
	protected int width;
	protected int visibleItems;
	protected int offset;
	protected int selected = -1;

	protected boolean showAll = true;
	protected boolean resized = false;
	protected boolean initialized = false;
	
	protected List<E> items = new ArrayList<>();
	protected ListItemRenderer<E> renderer = null;
	protected ItemClickListener<E> itemClickListener = null;
	
	protected Button btnUp;
	protected Button btnDown;
	
	protected int textColour = Color.WHITE.getRGB();
	protected int backgroundColour = Color.GRAY.getRGB();
	protected int borderColour = Color.BLACK.getRGB();
	
	/**
	 * Default constructor for the item list. Should be noted that the
	 * height is determined by how many visible items there are.
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width width of the list
	 * @param visibleItems how many items are visible
	 */
	public ItemList(int left, int top, int width, int visibleItems)
	{
		super(left, top);
		this.width = width;
		this.visibleItems = visibleItems;
	}

	public ItemList(int left, int top, int width, int visibleItems, boolean showAll)
	{
		this(left, top, width, visibleItems);
		this.showAll = showAll;
	}
	
	@Override
	public void init(Layout layout)
	{
		btnUp = new ButtonArrow(left + width - 12, top, ButtonArrow.Type.UP);
		btnUp.setEnabled(false);
		btnUp.setVisible(false);
		btnUp.setClickListener((c, mouseButton) -> scrollUp());
		layout.addComponent(btnUp);
		
		btnDown = new ButtonArrow(left + width - 12, top + getHeight() - 12, ButtonArrow.Type.DOWN);
		btnDown.setEnabled(false);
		btnDown.setVisible(false);
		btnDown.setClickListener((c, mouseButton) -> scrollDown());
		layout.addComponent(btnDown);

		updateButtons();
		updateComponent();

		initialized = true;
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
			int height = 13;
			if(renderer != null)
			{
				height = renderer.getHeight();
			}
			
			int size = getSize();

			/* Fill */
			Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + (size * height) + size, Color.LIGHT_GRAY.getRGB());

			/* Box */
			drawHorizontalLine(xPosition, xPosition + width - 1, yPosition, borderColour);
			drawVerticalLine(xPosition, yPosition, yPosition + (size * height) + size, borderColour);
			drawVerticalLine(xPosition + width - 1, yPosition, yPosition + (size * height) + size, borderColour);
			drawHorizontalLine(xPosition, xPosition + width - 1, yPosition + (size * height) + size, borderColour);

			/* Items */
			for(int i = 0; i < size - 1 && i < items.size(); i++)
			{
				E item = getItem(i);
				if(item != null)
				{
					if(renderer != null)
					{
						renderer.render(item, this, mc, xPosition + 1, yPosition + (i * (renderer.getHeight())) + 1 + i, width - 1, renderer.getHeight(), (i + offset) == selected);
						drawHorizontalLine(xPosition + 1, xPosition + width - 1, yPosition + (i * height) + i + height + 1, borderColour);
					}
					else
					{
						drawRect(xPosition + 1, yPosition + (i * 14) + 1, xPosition + width - 1, yPosition + 13 + (i * 14) + 1, (i + offset) != selected ? backgroundColour : Color.DARK_GRAY.getRGB());
						drawString(mc.fontRendererObj, item.toString(), xPosition + 3, yPosition + 3 + (i * 14), textColour);
						drawHorizontalLine(xPosition + 1, xPosition + width - 2, yPosition + (i * height) + i + height + 1, Color.LIGHT_GRAY.getRGB());
					}
				}
			}

			int i = size - 1;
			E item = getItem(i);
			if(item != null)
			{
				if(renderer != null)
				{
					renderer.render(item, this, mc, xPosition + 1, yPosition + (i * (renderer.getHeight())) + 1 + i, width - 1, renderer.getHeight(), (i + offset) == selected);
					drawHorizontalLine(xPosition + 1, xPosition + width - 1, yPosition + (i * height) + i + height + 1, borderColour);
				}
				else
				{
					drawRect(xPosition + 1, yPosition + (i * 14) + 1, xPosition + width - 1, yPosition + 13 + (i * 14) + 1, (i + offset) != selected ? backgroundColour : Color.DARK_GRAY.getRGB());
					drawString(mc.fontRendererObj, item.toString(), xPosition + 3, yPosition + 3 + (i * 14), textColour);
				}
			}

			if(items.size() > visibleItems)
			{
				drawRect(xPosition + width, yPosition, xPosition + width + 10, yPosition + (size * height) + size, Color.DARK_GRAY.getRGB());
				drawVerticalLine(xPosition + width + 10, yPosition + 11, yPosition + (size * height) + size - 11, borderColour);
			}
        }
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(!this.visible || !this.enabled)
			return;

		int height = renderer != null ? renderer.getHeight() : 13;
		int size = getSize();
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + size * height + size))
		{
			for(int i = 0; i < size && i < items.size(); i++)
			{
				if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition + (i * height) + i, xPosition + width, yPosition + (i * height) + i + height))
				{
					this.selected = i + offset;
					if(itemClickListener != null)
					{
						itemClickListener.onClick(items.get(selected), selected, mouseButton);
					}
				}
			}
		}
	}
	
	@Override
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction)
	{
		if(!this.visible || !this.enabled)
			return;

		int height = renderer != null ? renderer.getHeight() : 13;
		int size = getSize();
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + size * height + size))
		{
			if(direction)
			{
				scrollUp();
			}
			else
			{
				scrollDown();
			}
		}
	}

	private int getHeight()
	{
		int size = getSize();
		return (renderer != null ? renderer.getHeight() : 13) * size + size + 1;
	}

	private int getSize()
	{
		if(showAll) return visibleItems;
		return Math.max(2, Math.min(visibleItems, items.size()));
	}
	
	private void scrollUp()
	{
		if(offset > 0) {
			offset--;
			updateButtons();
		}
	}
	
	private void scrollDown()
	{
		if(getSize() + offset < items.size()) {
			offset++;
			updateButtons();
		}
	}

	private void updateButtons()
	{
		btnDown.setEnabled(getSize() + offset < items.size());
		btnUp.setEnabled(offset > 0);
	}

	private void updateComponent()
	{
		btnUp.setVisible(items.size() > visibleItems);
		btnDown.setVisible(items.size() > visibleItems);
		btnDown.top = top + getHeight() - 12;

		if(!resized && items.size() > visibleItems)
		{
			width -= 11;
			resized = true;
		}
		else if(resized && items.size() <= visibleItems)
		{
			width += 11;
			resized = false;
		}
	}

	/**
	 * Sets the custom item list renderer.
	 * 
	 * @param renderer the custom renderer
	 */
	public void setListItemRenderer(ListItemRenderer<E> renderer)
	{
		this.renderer = renderer;
	}
	
	/**
	 * Sets the item click listener for when an item is clicked.
	 * 
	 * @param itemClickListener the item click listener
	 */
	public void setItemClickListener(ItemClickListener<E> itemClickListener) 
	{
		this.itemClickListener = itemClickListener;
	}
	
	/**
	 * Appends an item to the list
	 * 
	 * @param e the item
	 */
	public void addItem(E e)
	{
		if(e != null)
		{
			items.add(e);
			if(initialized)
				updateComponent();
		}
	}
	
	/**
	 * Removes an item at the specified index
	 * 
	 * @param index the index to remove
	 */
	public void removeItem(int index)
	{
		if(index >= 0 && index < items.size())
		{
			items.remove(index);
			if(index == selected)
				selected = -1;
			if(initialized)
				updateComponent();
		}
	}
	
	/**
	 * Gets the items at the specified index
	 * 
	 * @param pos the item's index
	 * 
	 * @return the item
	 */
	public E getItem(int pos)
	{
		if(pos >= 0 && pos + offset < items.size())
		{
			return items.get(pos + offset);
		}
		return null;
	}
	
	/**
	 * Gets the selected item
	 * 
	 * @return the selected item
	 */
	public E getSelectedItem()
	{
		if(selected >= 0 && selected < items.size())
		{
			return items.get(selected);
		}
		return null;
	}
	
	/**
	 * Sets the selected item in the list using the index
	 * 
	 * @param index the index of the item
	 */
	public void setSelectedIndex(int index)
	{
		if(index < 0) index = -1;
		this.selected = index;
	}
	
	/**
	 * Gets the selected item's index
	 * 
	 * @return the index
	 */
	public int getSelectedIndex()
	{
		return selected;
	}
	
	/**
	 * Gets all items from the list
	 * 
	 * @return the items
	 */
	public List<E> getItems()
	{
		return items;
	}
	
	/**
	 * Removes all items from the list
	 */
	public void removeAll()
	{
		this.items.clear();
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
	 * @param color the border colour
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

	@Override
	public Iterator<E> iterator() 
	{
		return items.iterator();
	}
}
