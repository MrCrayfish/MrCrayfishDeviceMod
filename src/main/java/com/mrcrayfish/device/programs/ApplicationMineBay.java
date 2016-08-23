package com.mrcrayfish.device.programs;

import java.awt.Color;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class ApplicationMineBay extends Application
{
	private String[] categories = { "Building", "Combat", "Tools", "Food", "Materials", "Redstone", "Alchemy", "Rare", "Misc" };
	
	public ApplicationMineBay()
	{
		super(Reference.MOD_ID + "MineBay", "MineBay");
	}
	
	@Override
	public void init(int x, int y)
	{
		Layout home = new Layout(300, 150);
		home.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				gui.drawRect(x, y, x + width, y + 30, Color.GRAY.getRGB());
				gui.drawRect(x, y + 29, x + width, y + 30, Color.DARK_GRAY.getRGB());
				gui.drawRect(x, y + 30, x + 95, y + height, Color.LIGHT_GRAY.getRGB());
				gui.drawRect(x + 94, y + 30, x + 95, y + height, Color.GRAY.getRGB());
			}
		});
		
		Button btnAddItem = new Button("Add Item", x, y, 50, 5, 60, 20);
		home.addComponent(btnAddItem);
		
		Button btnViewItem = new Button("Your Auctions", x, y, 115, 5, 80, 20);
		home.addComponent(btnViewItem);
		
		Label labelCategories = new Label("Categories", x, y, 5, 34);
		labelCategories.setTextColour(Color.GRAY);
		labelCategories.setShadow(false);
		home.addComponent(labelCategories);
		
		ItemList<String> categories = new ItemList<String>(x, y, 5, 45, 70, 7);
		for(String category : this.categories) {
			categories.addItem(category);
		}
		home.addComponent(categories);
		
		Label labelItems = new Label("Items", x, y, 100, 34);
		labelItems.setTextColour(Color.GRAY);
		labelItems.setShadow(false);
		home.addComponent(labelItems);
		
		ItemList<ItemStack> items = new ItemList<ItemStack>(x, y, 100, 45, 180, 4);
		items.setListItemRenderer(new ListItemRenderer<ItemStack>(20)
		{
			@Override
			public void render(ItemStack e, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				if(selected) {
					gui.drawRect(x, y, x + width, y + height, Color.GRAY.getRGB());
				} else {
					gui.drawRect(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
				}
				
				
				mc.getRenderItem().renderItemIntoGUI(e, x + 2, y + 2);
				gui.drawString(mc.fontRendererObj, e.getDisplayName(), x + 24, y + 6, Color.WHITE.getRGB());
			}
		});
		home.addComponent(items);
		
		Button btnBuy = new Button("Buy", x, y, 100, 132, 50, 15);
		home.addComponent(btnBuy);
		
		setCurrentLayout(home);
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
