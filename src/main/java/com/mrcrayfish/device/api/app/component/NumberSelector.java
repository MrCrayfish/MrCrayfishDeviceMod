package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.text.DecimalFormat;

public class NumberSelector extends Component
{
	protected DecimalFormat format = new DecimalFormat("0");
	
	/* Component Properties */
	protected int current = 1;
	protected int min = 1;
	protected int max = 100;
	
	/* Display Properties */
	protected int width;
	
	/* Sub Components */
	protected Button btnUp;
	protected TextField display;
	protected Button btnDown;
	
	private boolean holding = false;
	private int holdCount = 0;
	
	/**
	 * Default NumberSelector constructor
	 * 
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width the width of the selector
	 */
	public NumberSelector(int left, int top, int width)
	{
		super(left, top);
		this.width = width;
	}
	
	@Override
	public void init(Layout layout)
	{
		btnUp = new Button(left, top, COMPONENTS_GUI, 111, 12, 8, 5);
		btnUp.setSize(width, 11);
		btnUp.setClickListener((c, mouseButton) ->
		{
            if(current < max)
            {
                current++;
                display.setText(format.format(current));
                updateButtons();
            }
        });
		layout.addComponent(btnUp);
		
		display = new TextField(left, top + 10, width);
		display.setEditable(false);
		display.setText(format.format(current));
		layout.addComponent(display);
		
		btnDown = new Button(left, top + 24, COMPONENTS_GUI, 119, 12, 8, 5);
		btnDown.setSize(width, 11);
		btnDown.setClickListener((c, mouseButton) ->
		{
            if(current > min)
            {
                current--;
                display.setText(format.format(current));
                updateButtons();
            }
        });
		layout.addComponent(btnDown);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {}
	
	@Override
	public void handleTick()
	{
		if(Mouse.isButtonDown(0))
		{
			if(holding)
			{
				btnUp.handleMouseClick(0, 0, 0);
				btnDown.handleMouseClick(0, 0, 0);
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
	
	/**
	 * Updates the selection buttons based on the current value
	 */
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
	
	/**
	 * Sets the minimum value a user can select. Throws an illegal
	 * argument exception if the value is less than 0 or more than
	 * the max value.
	 * 
	 * @param min the minimum value
	 */
	public void setMin(int min)
	{
		if(min < 0 || min > max) 
		{
			throw new IllegalArgumentException("The min value must be more or equal to zero and less than or equal to the max value");
		}
		
		this.min = min;
		
		if(current < min)
		{
			current = min;
		}
	}
	
	/**
	 * Sets the maximum value a user can select. Throws an illegal
	 * argument exception if the value is less than 0 or less than
	 * the min value.
	 * 
	 * @param max the maximum value
	 */
	public void setMax(int max)
	{
		if(max < 0 || max < min) 
		{
			throw new IllegalArgumentException("The max value must be more or equal to zero and more than or equal to the min value");
		}
		
		this.max = max;
		
		if(current > max)
		{
			current = max;
		}
	}
	
	/**
	 * Sets the current number selection. Throws an illegal
	 * argument exception if the value is not within the bounds
	 * of the min and max value.
	 * 
	 * @param current set the current number
	 */
	public void setNumber(int current)
	{
		if(current < min || current > max)
		{
			throw new IllegalArgumentException("The current value must be within range of the min and max boundaries");
		}
		this.current = current;
		display.setText(Integer.toString(current));
	}
	
	/**
	 * Gets the current number selection
	 * 
	 * @return the current number
	 */
	public int getNumber()
	{
		return current;
	}
	
	/**
	 * Sets the formatting of the display. Read the {@link DecimalFormat}
	 * documentation for pattern syntax.
	 * 
	 * @param format the format
	 */
	public void setFormat(DecimalFormat format)
	{
		this.format = format;
	}
}
