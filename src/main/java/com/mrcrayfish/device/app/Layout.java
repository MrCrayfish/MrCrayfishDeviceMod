package com.mrcrayfish.device.app;

import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.TextArea;
import com.mrcrayfish.device.app.components.TextField;

public class Layout
{
	public List<Component> components;
	
	public Layout() 
	{
		this.components = new ArrayList<Component>();
	}
	
	public void addComponent(Component c)
	{
		if(c != null)
		{
			this.components.add(c);
			c.init(this);
		}
	}
	
	public List<Component> getComponents()
	{
		return components;
	}
	
	public void hide()
	{
		for(Component c : components)
		{
			c.visible = false;
		}
	}
	
	public void show()
	{
		for(Component c : components)
		{
			c.visible = true;
		}
	}
}
