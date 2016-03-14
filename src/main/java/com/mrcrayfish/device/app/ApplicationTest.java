package com.mrcrayfish.device.app;

import com.mrcrayfish.device.app.components.Button;
import com.mrcrayfish.device.app.components.ItemList;
import com.mrcrayfish.device.app.components.TextField;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class ApplicationTest extends Application
{
	public ItemList<String> list;
	private TextField input;
	private Button add, delete;
	
	public ApplicationTest() 
	{
		super("test_app", "Test App", 200, 200);
	}
	
	@Override
	public void init(int x, int y) 
	{
		super.init(x, y);
		
		list = new ItemList<String>(x, y, 5, 5, 100, 6);
		this.addComponent(list);
		
		input = new TextField(Minecraft.getMinecraft().fontRendererObj, x, y, 130, 5, 50);
		this.addComponent(input);
		
		add = new Button("Add", x, y, 130, 25, 50, 20);
		this.addComponent(add);
		
		delete = new Button("Delete", x, y, 130, 50, 50, 20);
		this.addComponent(delete);
	}
	
	@Override
	public void handleButtonClick(Button button) 
	{
		super.handleButtonClick(button);
		
		if(button == add)
		{
			if(input.getText().length() > 0)
			{
				list.addItem(input.getText());
				input.setText("");
			}
		}
		else if(button == delete)
		{
			if(list.getSelectedIndex() != -1)
			{
				list.removeItem(list.getSelectedIndex());
			}
		}
	}

	@Override
	public void load(NBTTagCompound tagCompound) 
	{
		
	}

	@Override
	public void save(NBTTagCompound tagCompound) 
	{
		
	}

}
