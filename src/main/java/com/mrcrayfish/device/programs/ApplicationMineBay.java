package com.mrcrayfish.device.programs;

import java.awt.Color;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.api.utils.RenderUtil;

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
	
	private Layout layoutAdd;
	
	public ApplicationMineBay()
	{
		super(Reference.MOD_ID + "MineBay", "MineBay");
	}
	
	@Override
	public void init(int x, int y)
	{
		Layout home = new Layout(300, 145);
		home.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height)
			{
				gui.drawRect(x, y, x + width, y + 25, Color.GRAY.getRGB());
				gui.drawRect(x, y + 24, x + width, y + 25, Color.DARK_GRAY.getRGB());
				gui.drawRect(x, y + 25, x + 95, y + height, Color.LIGHT_GRAY.getRGB());
				gui.drawRect(x + 94, y + 25, x + 95, y + height, Color.GRAY.getRGB());
			}
		});
		
		Button btnAddItem = new Button("Add Item", x, y, 50, 5, 60, 15);
		home.addComponent(btnAddItem);
		
		Button btnViewItem = new Button("Your Auctions", x, y, 115, 5, 80, 15);
		home.addComponent(btnViewItem);
		
		Label labelBalance = new Label("Balance", x, y, 295, 3);
		labelBalance.setAlignment(Label.ALIGN_RIGHT);
		home.addComponent(labelBalance);
		
		final Label labelMoney = new Label("$0", x, y, 295, 13);
		labelMoney.setAlignment(Label.ALIGN_RIGHT);
		labelMoney.setScale(1);
		labelMoney.setShadow(false);
		home.addComponent(labelMoney);
		
		Label labelCategories = new Label("Categories", x, y, 5, 29);
		labelCategories.setShadow(false);
		home.addComponent(labelCategories);
		
		ItemList<String> categories = new ItemList<String>(x, y, 5, 40, 70, 7);
		for(String category : this.categories) {
			categories.addItem(category);
		}
		home.addComponent(categories);
		
		Label labelItems = new Label("Items", x, y, 100, 29);
		labelItems.setShadow(false);
		home.addComponent(labelItems);
		
		ItemList<ItemStack> items = new ItemList<ItemStack>(x, y, 100, 40, 180, 4);
		items.setListItemRenderer(new ListItemRenderer<ItemStack>(20)
		{
			@Override
			public void render(ItemStack e, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				if(selected) 
				{
					gui.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.getRGB());
				} 
				else 
				{
					gui.drawRect(x, y, x + width, y + height, Color.GRAY.getRGB());
				}
				
				RenderUtil.renderItem(x + 2, y + 2, e);
				gui.drawString(mc.fontRendererObj, e.getDisplayName(), x + 24, y + 6, Color.WHITE.getRGB());
			}
		});
		home.addComponent(items);
		
		items.addItem(new ItemStack(Items.painting));
		
		ItemStack sugar = new ItemStack(Items.sugar);
		sugar.setStackDisplayName(EnumChatFormatting.AQUA + "The Good Stuff");
		items.addItem(sugar);
		
		items.addItem(new ItemStack(Items.saddle));
		
		Button btnBuy = new Button("Buy", x, y, 100, 127, 50, 15);
		btnBuy.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				labelMoney.setText("HELLLLLLO");
			}
		});
		home.addComponent(btnBuy);
		
		setCurrentLayout(home);
		
		BankUtil.getBalance(new Callback()
		{
			@Override
			public void execute(NBTTagCompound nbt, boolean success)
			{
				if(success) 
				{
					labelMoney.setText("$" + nbt.getInteger("balance"));
				}
			}
		});
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
