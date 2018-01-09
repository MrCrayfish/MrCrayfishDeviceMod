package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class CheckBox extends Component implements RadioGroup.Item
{
	protected String name;
	protected boolean checked = false;
	protected RadioGroup group = null;
	
	protected ClickListener listener = null;
	
	protected int textColor = Color.WHITE.getRGB();
	protected int backgroundColor = Color.GRAY.getRGB();
	protected int borderColor = Color.BLACK.getRGB();
	protected int checkedColor = Color.DARK_GRAY.getRGB();
	
	/**
	 * Default check box constructor
	 * 
	 * @param name the name of the check box
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 */
	public CheckBox(String name, int left, int top) 
	{
		super(left, top);
		this.name = name;
	}
	
	/**
	 * Sets the radio group for this button.
	 * 
	 * @param group the radio group.
	 */
	public void setRadioGroup(RadioGroup group)
	{
		this.group = group;
		this.group.add(this);
	}
	
	/**
	 * Sets the click listener. Use this to handle custom actions
	 * when you press the check box.
	 * 
	 * @param listener the click listener
	 */
	public void setClickListener(ClickListener listener) 
	{
		this.listener = listener;
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
        {
        	if(group == null)
			{
				drawRect(xPosition, yPosition, xPosition + 10, yPosition + 10, borderColor);
				drawRect(xPosition + 1, yPosition + 1, xPosition + 9, yPosition + 9, backgroundColor);
				if(checked)
				{
					drawRect(xPosition + 2, yPosition + 2, xPosition + 8, yPosition + 8, checkedColor);
				}
			}
			else
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(COMPONENTS_GUI);
				drawTexturedModalRect(xPosition, yPosition, checked ? 10 : 0, 60, 10, 10);
			}
			drawString(mc.fontRenderer, name, xPosition + 12, yPosition + 1, textColor);
        }
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(!this.visible || !this.enabled)
			return;
		
		if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + 10, yPosition + 10))
		{
			if(group != null)
			{
				group.deselect();
			}
			this.checked = !checked;
			if(listener != null)
			{
				listener.onClick(mouseX, mouseY, mouseButton);
			}
		}
	}
	
	@Override
	public boolean isSelected() 
	{
		return checked;
	}
	
	@Override
	public void setSelected(boolean enabled) 
	{
		this.checked = enabled;
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
	 * @param color the background color
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
	
	/**
	 * Sets the checked color for this component
	 * 
	 * @param color the checked color
	 */
	public void setCheckedColor(Color color)
	{
		this.checkedColor = color.getRGB();
	}
}
