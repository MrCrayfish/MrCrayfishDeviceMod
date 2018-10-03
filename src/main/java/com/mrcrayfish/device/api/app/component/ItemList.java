package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
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
	protected boolean loading = false;

	protected List<E> items = NonNullList.create();
	protected ListItemRenderer<E> renderer = null;
	protected ItemClickListener<E> itemClickListener = null;
	
	protected Button btnUp;
	protected Button btnDown;
	protected Layout layoutLoading;
	
	protected int textColor = Color.WHITE.getRGB();
	protected int backgroundColor = Color.GRAY.getRGB();
	protected int borderColor = Color.BLACK.getRGB();
	private static final int LOADING_BACKGROUND = new Color(0F, 0F, 0F, 0.5F).getRGB();

	private Comparator<E> sorter = null;

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
		btnUp = new Button(left + width - 12, top, Icons.CHEVRON_UP);
		btnUp.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton == 0) scrollUp();
		});
		btnUp.setEnabled(false);
		btnUp.setVisible(false);
		btnUp.setSize(12, 12);
		layout.addComponent(btnUp);

		btnDown = new Button(left + width - 12, top + getHeight() - 12, Icons.CHEVRON_DOWN);
		btnDown.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(mouseButton == 0) scrollDown();
        });
		btnDown.setEnabled(false);
		btnDown.setVisible(false);
		btnDown.setSize(12, 12);
		layout.addComponent(btnDown);

		layoutLoading = new Layout(left, top, getWidth(), getHeight());
		layoutLoading.setVisible(loading);
		layoutLoading.addComponent(new Spinner((layoutLoading.width - 12) / 2, (layoutLoading.height - 12) / 2));
		layoutLoading.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			Gui.drawRect(x, y, x + width, y + height, LOADING_BACKGROUND);
		});
		layout.addComponent(layoutLoading);

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

			Color bgColor = new Color(getColorScheme().getBackgroundColor());
			Color borderColor = bgColor.darker().darker();

			/* Fill */
			Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + (size * height) + size, bgColor.getRGB());

			/* Box */
			drawHorizontalLine(xPosition, xPosition + width - 1, yPosition, borderColor.getRGB());
			drawVerticalLine(xPosition, yPosition, yPosition + (size * height) + size, borderColor.getRGB());
			drawVerticalLine(xPosition + width - 1, yPosition, yPosition + (size * height) + size, borderColor.getRGB());
			drawHorizontalLine(xPosition, xPosition + width - 1, yPosition + (size * height) + size, borderColor.getRGB());

			/* Items */
			for(int i = 0; i < size - 1 && i < items.size(); i++)
			{
				E item = getItem(i);
				if(item != null)
				{
					if(renderer != null)
					{
						renderer.render(item, this, mc, xPosition + 1, yPosition + (i * (renderer.getHeight())) + 1 + i, width - 2, renderer.getHeight(), (i + offset) == selected);
						drawHorizontalLine(xPosition + 1, xPosition + width - 1, yPosition + (i * height) + i + height + 1, borderColor.getRGB());
					}
					else
					{
						drawRect(xPosition + 1, yPosition + (i * 14) + 1, xPosition + width - 1, yPosition + 13 + (i * 14) + 1, (i + offset) != selected ? bgColor.brighter().getRGB() : bgColor.brighter().brighter().getRGB());
						drawString(mc.fontRenderer, item.toString(), xPosition + 3, yPosition + 3 + (i * 14), textColor);
						drawHorizontalLine(xPosition + 1, xPosition + width - 2, yPosition + (i * height) + i + height + 1, borderColor.getRGB());
					}
				}
			}

			int i = size - 1;
			E item = getItem(i);
			if(item != null)
			{
				if(renderer != null)
				{
					renderer.render(item, this, mc, xPosition + 1, yPosition + (i * (renderer.getHeight())) + 1 + i, width - 2, renderer.getHeight(), (i + offset) == selected);
					drawHorizontalLine(xPosition + 1, xPosition + width - 1, yPosition + (i * height) + i + height + 1, borderColor.getRGB());
				}
				else
				{
					drawRect(xPosition + 1, yPosition + (i * 14) + 1, xPosition + width - 1, yPosition + 13 + (i * 14) + 1, (i + offset) != selected ? bgColor.brighter().getRGB() : bgColor.brighter().brighter().getRGB());
					drawString(Laptop.fontRenderer, item.toString(), xPosition + 3, yPosition + 3 + (i * 14), textColor);
				}
			}

			if(items.size() > visibleItems)
			{
				drawRect(xPosition + width, yPosition, xPosition + width + 10, yPosition + (size * height) + size, Color.DARK_GRAY.getRGB());
				drawVerticalLine(xPosition + width + 10, yPosition + 11, yPosition + (size * height) + size - 11, borderColor.getRGB());
			}
        }
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(!this.visible || !this.enabled || this.loading)
			return;

		int height = renderer != null ? renderer.getHeight() : 13;
		int size = getSize();
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + size * height + size))
		{
			for(int i = 0; i < size && i < items.size(); i++)
			{
				if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition + 1, yPosition + (i * height) + i, xPosition + width - 1, yPosition + (i * height) + i + height))
				{
					if(mouseButton == 0) this.selected = i + offset;
					if(itemClickListener != null)
					{
						itemClickListener.onClick(items.get(i + offset), i + offset, mouseButton);
					}
				}
			}
		}
	}
	
	@Override
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction)
	{
		if(!this.visible || !this.enabled || this.loading)
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

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		int size = getSize();
		return (renderer != null ? renderer.getHeight() : 13) * size + size + 1;
	}

	private int getSize()
	{
		if(showAll) return visibleItems;
		return Math.max(1, Math.min(visibleItems, items.size()));
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

	private void updateScroll()
	{
		if(offset + visibleItems > items.size())
		{
			offset = Math.max(0, items.size() - visibleItems);
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
	public void addItem(@Nonnull E e)
	{
		items.add(e);
		sort();
		if(initialized)
		{
			updateButtons();
			updateComponent();
		}
	}

	/**
	 * Appends an item to the list
	 *
	 * @param newItems the items
	 */
	public void setItems(List<E> newItems)
	{
		items.clear();
		items.addAll(newItems);
		sort();
		if(initialized)
		{
			offset = 0;
			updateButtons();
			updateComponent();
		}
	}
	
	/**
	 * Removes an item at the specified index
	 * 
	 * @param index the index to remove
	 */
	public E removeItem(int index)
	{
		if(index >= 0 && index < items.size())
		{
			E e = items.remove(index);
			if(index == selected)
				selected = -1;
			if(initialized)
			{
				updateButtons();
				updateComponent();
				updateScroll();
			}
			return e;
		}
		return null;
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
	@Nullable
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
	 * Gets all items from the list. Do not use this to remove items from the item list, instead use
	 * {@link #removeItem(int)} otherwise it will cause scroll issues.
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
		this.selected = -1;
		if(initialized)
		{
			updateButtons();
			updateComponent();
			updateScroll();
		}
	}
	
	/**
	 * Sets the text color for this component
	 * 
	 * @param color the text color
	 */
	public void setTextColor(Color color)
	{
		this.textColor = color.getRGB();
	}
	
	/**
	 * Sets the background color for this component
	 * 
	 * @param color the border color
	 */
	public void setBackgroundColor(Color color)
	{
		this.backgroundColor = color.getRGB();
	}
	
	/**
	 * Sets the border color for this component
	 * 
	 * @param color the border color
	 */
	public void setBorderColor(Color color)
	{
		this.borderColor = color.getRGB();
	}

	public void setLoading(boolean loading)
	{
		this.loading = loading;
		if(initialized)
		{
			layoutLoading.setVisible(loading);
		}
	}

	/**
	 * Sets the sorter for this item list and updates straight away
	 * @param sorter the comparator to sort the list by
	 */
	public void sortBy(Comparator<E> sorter)
	{
		this.sorter = sorter;
		sort();
	}

	/**
	 * Sorts the list
	 */
	public void sort()
	{
		if(sorter != null)
		{
			Collections.sort(items, sorter);
		}
	}

	@Override
	public Iterator<E> iterator() 
	{
		return items.iterator();
	}
}
