package com.mrcrayfish.device.app.components;

import java.util.HashSet;
import java.util.Set;

public class RadioGroup
{
	private Set<IRadioGroupItem> group = new HashSet<IRadioGroupItem>();
	
	public void add(IRadioGroupItem item)
	{
		this.group.add(item);
	}

	public void unselect()
	{
		for(IRadioGroupItem item : group)
		{
			item.setSelected(false);
		}
	}
}