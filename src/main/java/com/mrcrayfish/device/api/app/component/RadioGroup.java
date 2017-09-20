package com.mrcrayfish.device.api.app.component;

import java.util.HashSet;
import java.util.Set;

public class RadioGroup
{
	protected Set<Item> group = new HashSet<>();
	
	/***
	 * Adds an item to this group
	 * 
	 * @param item the item
	 */
	public void add(Item item)
	{
		this.group.add(item);
	}

	/**
	 * Deselects all items in this group
	 */
	public void deselect()
	{
		for(Item item : group)
		{
			item.setSelected(false);
		}
	}
	
	public interface Item {
		
		/**
		 * Gets if this item is selected
		 * 
		 * @return if this item is selected
		 */
		boolean isSelected();
		
		/**
		 * Sets this item as selected or not.
		 * 
		 * @param enabled selected or not
		 */
		void setSelected(boolean enabled);
	}
}