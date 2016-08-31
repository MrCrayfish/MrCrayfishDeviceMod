package com.mrcrayfish.device.api.app.component;

import java.text.DecimalFormat;

import org.lwjgl.input.Mouse;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;

public class NumberSelector extends Component
{
	protected DecimalFormat format = new DecimalFormat("0");
	
	protected int current = 1;
	protected int min = 1;
	protected int max = 100;
	protected int width;
	
	protected Button btnUp;
	protected TextField display;
	protected Button btnDown;
	
	private boolean holding = false;
	private int holdCount = 0;
	
	public NumberSelector(int left, int top, int width)
	{
		super(left, top);
		this.width = width;
	}
	
	@Override
	public void init(Layout layout)
	{
		btnUp = new Button(left, top, width, 11, COMPONENTS_GUI, 111, 12, 8, 5);
		btnUp.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(current < max)
				{
					current++;
					display.setText(format.format(current));
					updateButtons();
				}
			}
		});
		layout.addComponent(btnUp);
		
		display = new TextField(left, top + 10, width);
		display.setEditable(false);
		display.setText(format.format(current));
		layout.addComponent(display);
		
		btnDown = new Button(left, top + 24, width, 11, COMPONENTS_GUI, 119, 12, 8, 5);
		btnDown.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(current > min)
				{
					current--;
					display.setText(format.format(current));
					updateButtons();
				}
			}
		});
		layout.addComponent(btnDown);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks) {}
	
	@Override
	public void handleTick()
	{
		if(Mouse.isButtonDown(0))
		{
			if(holding)
			{
				btnUp.handleClick(0, 0, 0);
				btnDown.handleClick(0, 0, 0);
			}
			else
			{
				holdCount++;
				if(holdCount > 10)
				{
					holding = true;
				}
			}
		}
		else
		{
			holdCount = 0;
			holding = false;
		}
	}
	
	public void updateButtons()
	{
		btnUp.setEnabled(true);
		btnDown.setEnabled(true);
		if(current == max)
		{
			btnUp.setEnabled(false);
		}
		if(current == min)
		{
			btnDown.setEnabled(false);
		}
	}
	
	public void setMin(int min)
	{
		if(min < 0) 
		{
			throw new IllegalArgumentException("The min value must be more or equal to zero");
		}
		
		this.min = min;
		
		if(current < min)
		{
			current = min;
		}
	}
	
	public void setMax(int max)
	{
		if(max < 0 || max < min) 
		{
			throw new IllegalArgumentException("The max value must be more or equal to zero and more than the min value");
		}
		
		this.max = max;
		
		if(current > max)
		{
			current = max;
		}
	}
	
	public void setNumber(int current)
	{
		if(current < min || current > max)
		{
			throw new IllegalArgumentException("The current value must be within range of the min and max boundaries");
		}
		this.current = current;
		display.setText(Integer.toString(current));
	}
	
	public int getNumber()
	{
		return current;
	}
	
	public void setFormat(DecimalFormat format)
	{
		this.format = format;
	}
}
