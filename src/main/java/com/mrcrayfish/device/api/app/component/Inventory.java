package com.mrcrayfish.device.api.app.component;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;

/**
 * A component that allows you "access" to the players inventory. Now why access
 * is in quotes is because it's client side only. If you want to process anything,
 * you'll have to sendTask the selected item slot to the server and process it there.
 * You can use a {@link Task} to perform this.
 * 
 * @author MrCrayfish
 */
public class Inventory extends Component
{
	protected static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	
	protected int selectedColour = new Color(1F, 1F, 0F, 0.15F).getRGB();
	protected int hoverColour = new Color(1F, 1F, 1F, 0.15F).getRGB();
	
	protected int selected = -1;
	
	protected ClickListener clickListener = null;
	
	public Inventory(int left, int top)
	{
		super(left, top);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		if (this.visible)
		{
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
			RenderUtil.drawRectWithTexture(xPosition, yPosition, 7, 139, 162, 54, 162, 54);

			InventoryPlayer inventory = mc.player.inventory;
			for(int i = 9; i < inventory.getSizeInventory() - 4; i++)
			{
				int offsetX = (i % 9) * 18;
				int offsetY = (i / 9) * 18 - 18;

				if(selected == i)
				{
					Gui.drawRect(xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 18, yPosition + offsetY + 18, selectedColour);
				}

				if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 17, yPosition + offsetY + 17))
				{
					Gui.drawRect(xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 18, yPosition + offsetY + 18, hoverColour);
				}

				ItemStack stack = inventory.getStackInSlot(i);
				if(!stack.isEmpty())
				{
					RenderUtil.renderItem(xPosition + offsetX + 1, yPosition + offsetY + 1, stack, true);
				}
			}
		}
	}
	
	@Override
	public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive)
	{
		if (this.visible)
		{
			for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 9; j++)
				{
					int x = xPosition + (j * 18) - 1;
					int y = yPosition + (i * 18) - 1;
					if(GuiHelper.isMouseInside(mouseX, mouseY, x, y, x + 18, y + 18))
					{
						ItemStack stack = mc.player.inventory.getStackInSlot((i * 9) + j + 9);
						if(!stack.isEmpty())
						{
							laptop.drawHoveringText(Arrays.asList(new String[]{stack.getDisplayName()}), mouseX, mouseY);
						}
						return;
					}
				}
			}
		}
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(!this.visible || !this.enabled)
			return;

		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				int x = xPosition + (j * 18) - 1;
				int y = yPosition + (i * 18) - 1;
				if(GuiHelper.isMouseInside(mouseX, mouseY, x, y, x + 18, y + 18))
				{
					this.selected = (i * 9) + j + 9;
					if(clickListener != null)
					{
						clickListener.onClick(mouseX, mouseY, mouseButton);
					}
					return;
				}
			}
		}
	}
	
	/**
	 * Gets the selected slot index
	 * 
	 * @return the slot index
	 */
	public int getSelectedSlotIndex()
	{
		return selected;
	}
	
	/**
	 * Sets the click listener for when an item is clicked
	 * 
	 * @param clickListener the click listener
	 */
	public void setClickListener(ClickListener clickListener)
	{
		this.clickListener = clickListener;
	}
	
	/**
	 * Sets the colour displayed when an item is selected
	 * 
	 * @param selectedColour the selected colour
	 */
	public void setSelectedColour(int selectedColour)
	{
		this.selectedColour = selectedColour;
	}
	
	/**
	 * Sets the colour displayed when a mouse is hovering an item
	 * 
	 * @param hoverColour the hover colour
	 */
	public void setHoverColour(int hoverColour)
	{
		this.hoverColour = hoverColour;
	}
}
