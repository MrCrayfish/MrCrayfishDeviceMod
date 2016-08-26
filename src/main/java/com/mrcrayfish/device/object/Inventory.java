package com.mrcrayfish.device.object;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class Inventory extends Component
{
	private int selected = -1;
	
	public Inventory(int x, int y, int left, int top)
	{
		super(x, y, left, top);
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		InventoryPlayer inventory = mc.thePlayer.inventory;
		for(int i = 9; i < inventory.getSizeInventory() - 4; i++)
		{
			int offsetX = (i % 9) * 20;
			int offsetY = (i / 9) * 20 - 20;
			
			if(selected == i)
			{
				laptop.drawRect(xPosition + offsetX - 1, yPosition + offsetY - 1, xPosition + offsetX + 17, yPosition + offsetY + 17, Color.DARK_GRAY.getRGB());
				laptop.drawRect(xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 16, yPosition + offsetY + 16, Color.GRAY.getRGB());
			}
			else
			{
				laptop.drawRect(xPosition + offsetX - 1, yPosition + offsetY - 1, xPosition + offsetX + 17, yPosition + offsetY + 17, Color.DARK_GRAY.getRGB());
				laptop.drawRect(xPosition + offsetX, yPosition + offsetY, xPosition + offsetX + 16, yPosition + offsetY + 16, Color.LIGHT_GRAY.getRGB());
			}
			
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack != null)
			{
				RenderUtil.renderItem(xPosition + offsetX, yPosition + offsetY, stack);
			}
		}
	}
	
	@Override
	public void handleClick(int mouseX, int mouseY, int mouseButton)
	{
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				int x = xPosition + (j * 20);
				int y = yPosition + (i * 20);
				if(GuiHelper.isMouseInside(mouseX, mouseY, x - 1, y - 1, x + 17, y + 17))
				{
					this.selected = (i * 9) + j + 9;
					return;
				}
			}
		}
	}

}
