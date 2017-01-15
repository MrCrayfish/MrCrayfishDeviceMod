package com.mrcrayfish.device.api.app.component;

import java.util.HashSet;
import java.util.Set;

public class RadioGroup
{
	protected Set<Item> group = new HashSet<Item>();
	
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
	 * Unselects all items in this group 
	 */
	public void unselect()
	{
		for(Item item : group)
		{
			item.setSelected(false);
		}
	}
	
	public static interface Item {
		
		/**
		 * Gets if this item is selected
		 * 
		 * @return if this item is selected
		 */
		public boolean isSelected();
		
		/**
		 * Sets this item as selected or not.
		 * 
		 * @param enabled selected or not
		 */
		public void setSelected(boolean enabled);
	}
}