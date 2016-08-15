package com.mrcrayfish.device.api.app.component;

import java.util.HashSet;
import java.util.Set;

public class RadioGroup
{
	protected Set<Item> group = new HashSet<Item>();
	
	public void add(Item item)
	{
		this.group.add(item);
	}

	public void unselect()
	{
		for(Item item : group)
		{
			item.setSelected(false);
		}
	}
	
	public static interface Item {
		
		public boolean isSelected();
		
		public void setSelected(boolean enabled);
	}
}