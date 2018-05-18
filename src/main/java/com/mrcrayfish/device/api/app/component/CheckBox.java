package com.mrcrayfish.device.api.app.component;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CheckBox extends Component implements RadioGroup.Item
{
	protected String name;
	protected boolean checked = false;
	protected RadioGroup group = null;
	
	protected ClickListener listener = null;
	
	protected int textColour = -1;
	protected int backgroundColour = -1;
	protected int borderColour = -1;
	protected int checkedColour = -1;
	
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
				Color bgColor = new Color(getColourScheme().getBackgroundColour());
				drawRect(xPosition, yPosition, xPosition + 10, yPosition + 10, color(borderColour, bgColor.darker().darker().getRGB()));
				drawRect(xPosition + 1, yPosition + 1, xPosition + 9, yPosition + 9, color(backgroundColour, bgColor.getRGB()));
				if(checked)
				{
					drawRect(xPosition + 2, yPosition + 2, xPosition + 8, yPosition + 8, color(checkedColour, bgColor.brighter().brighter().getRGB()));
				}
			}
			else
			{
				Color bgColor = new Color(getColourScheme().getBackgroundColour()).brighter().brighter();
				float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
				bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], 1.0F));
				GL11.glColor4f(bgColor.getRed() / 255F, bgColor.getGreen() / 255F, bgColor.getBlue() / 255F, 1.0F);
				mc.getTextureManager().bindTexture(COMPONENTS_GUI);
				drawTexturedModalRect(xPosition, yPosition, checked ? 10 : 0, 60, 10, 10);
			}
			drawString(mc.fontRenderer, name, xPosition + 12, yPosition + 1, color(textColour, getColourScheme().getTextColour()));
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
	 * Sets the text colour for this component
	 * 
	 * @param color the text colour
	 */
	public void setTextColour(Color color) 
	{
		this.textColour = color.getRGB();
	}
	
	/**
	 * Sets the background colour for this component
	 * 
	 * @param color the background colour
	 */
	public void setBackgroundColour(Color color) 
	{
		this.backgroundColour = color.getRGB();
	}
	
	/**
	 * Sets the border colour for this component
	 * 
	 * @param color the border colour
	 */
	public void setBorderColour(Color color) 
	{
		this.borderColour = color.getRGB();
	}
	
	/**
	 * Sets the checked colour for this component
	 * 
	 * @param color the checked colour
	 */
	public void setCheckedColour(Color color)
	{
		this.checkedColour = color.getRGB();
	}
}
