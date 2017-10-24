package com.mrcrayfish.device.programs.auction;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.programs.auction.object.AuctionItem;
import com.mrcrayfish.device.programs.auction.task.TaskAddAuction;
import com.mrcrayfish.device.programs.auction.task.TaskBuyItem;
import com.mrcrayfish.device.programs.auction.task.TaskGetAuctions;
import com.mrcrayfish.device.util.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class ApplicationMineBay extends Application
{
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private static final ResourceLocation MINEBAY_ASSESTS = new ResourceLocation("cdm:textures/gui/minebay.png");
	
	private static final ItemStack EMERALD = new ItemStack(Items.EMERALD);
	
	private String[] categories = { "Building", "Combat", "Tools", "Food", "Materials", "Redstone", "Alchemy", "Rare", "Misc" };

	private Layout layoutMyAuctions;
	private ItemList<AuctionItem> items;

	/* Add Item Layout */
	private Layout layoutSelectItem;
	private Inventory inventory;
	private Button buttonAddCancel;
	private Button buttonAddNext;
	
	/* Set Amount and Price Layout */
	private Layout layoutAmountAndPrice;
	private Label labelAmount;
	private NumberSelector selectorAmount;
	private Label labelPrice;
	private NumberSelector selectorPrice;
	private Button buttonAmountAndPriceBack;
	private Button buttonAmountAndPriceCancel;
	private Button buttonAmountAndPriceNext;
	
	/* Set Duration Layout */
	private Layout layoutDuration;
	private Label labelHours;
	private Label labelMinutes;
	private Label labelSeconds;
	private NumberSelector selectorHours;
	private NumberSelector selectorMinutes;
	private NumberSelector selectorSeconds;
	private Button buttonDurationBack;
	private Button buttonDurationCancel;
	private Button buttonDurationAdd;
	
	public ApplicationMineBay()
	{
		//super(Reference.MOD_ID + "MineBay", "MineBay");
	}
	
	@Override
	public void onTick()
	{
		super.onTick();
		AuctionManager.INSTANCE.tick();
	}
	
	@Override
	public void init()
	{
		getCurrentLayout().setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
			{
				Gui.drawRect(x, y, x + width, y + 25, Color.GRAY.getRGB());
				Gui.drawRect(x, y + 24, x + width, y + 25, Color.DARK_GRAY.getRGB());
				Gui.drawRect(x, y + 25, x + 95, y + height, Color.LIGHT_GRAY.getRGB());
				Gui.drawRect(x + 94, y + 25, x + 95, y + height, Color.GRAY.getRGB());
				
				mc.getTextureManager().bindTexture(MINEBAY_ASSESTS);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				RenderUtil.drawRectWithTexture(x + 5, y + 6, 0, 0, 61, 11, 61, 12);
			}
		});
		
		Button btnAddItem = new Button(70, 5, "Add Item");
		btnAddItem.setSize(60, 15);
		btnAddItem.setClickListener((c, mouseButton) -> setCurrentLayout(layoutSelectItem));
		super.addComponent(btnAddItem);

		Button btnViewItem = new Button(135, 5, "Your Auctions");
		btnViewItem.setSize(80, 15);
		btnViewItem.setClickListener((c, mouseButton) -> {
			TaskGetAuctions task = new TaskGetAuctions(Minecraft.getMinecraft().player.getUniqueID());
			task.setCallback((nbt, success) -> {
                items.removeAll();
                for(AuctionItem item : AuctionManager.INSTANCE.getItems()) {
                    items.addItem(item);
                }
            });
			TaskManager.sendTask(task);
		});
		super.addComponent(btnViewItem);
		
		Label labelBalance = new Label("Balance", 295, 3);
		labelBalance.setAlignment(Label.ALIGN_RIGHT);
		super.addComponent(labelBalance);
		
		final Label labelMoney = new Label("$0", 295, 13);
		labelMoney.setAlignment(Label.ALIGN_RIGHT);
		labelMoney.setScale(1);
		labelMoney.setShadow(false);
		super.addComponent(labelMoney);
		
		Label labelCategories = new Label("Categories", 5, 29);
		labelCategories.setShadow(false);
		super.addComponent(labelCategories);
		
		ItemList<String> categories = new ItemList<String>(5, 40, 70, 7);
		for(String category : this.categories) {
			categories.addItem(category);
		}
		super.addComponent(categories);
		
		Label labelItems = new Label("Items", 100, 29);
		labelItems.setShadow(false);
		super.addComponent(labelItems);
		
		items = new ItemList<AuctionItem>(100, 40, 180, 4);
		items.setListItemRenderer(new ListItemRenderer<AuctionItem>(20)
		{
			@Override
			public void render(AuctionItem e, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				if(selected) 
				{
					Gui.drawRect(x, y, x + width, y + height, Color.DARK_GRAY.getRGB());
				} 
				else 
				{
					Gui.drawRect(x, y, x + width, y + height, Color.GRAY.getRGB());
				}
				
				RenderUtil.renderItem(x + 2, y + 2, e.getStack(), true);
				
				GlStateManager.pushMatrix();
				{
					GlStateManager.translate(x + 24, y + 4, 0);
					GlStateManager.scale(0.666, 0.666, 0);
					mc.fontRendererObj.drawString(e.getStack().getDisplayName(), 0, 0, Color.WHITE.getRGB(), false);
					mc.fontRendererObj.drawString(TimeUtil.getTotalRealTime(e.getTimeLeft()), 0, 11, Color.LIGHT_GRAY.getRGB(), false);
				}
				GlStateManager.popMatrix();
				
				String price = "$" + e.getPrice();
				mc.fontRendererObj.drawString(price, x - mc.fontRendererObj.getStringWidth(price) + width - 5, y + 6, Color.YELLOW.getRGB());
			}
		});
		super.addComponent(items);
		
		Button btnBuy = new Button(100, 127, "Buy");
		btnBuy.setSize(50, 15);
		btnBuy.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				final Dialog.Confirmation dialog = new Dialog.Confirmation();
				dialog.setPositiveText("Buy");
				dialog.setPositiveListener((c13, mouseButton13) -> {
                    final int index = items.getSelectedIndex();
                    if(index == -1) return;

                    AuctionItem item = items.getItem(index);
                    if(item != null)
                    {
                        TaskBuyItem task = new TaskBuyItem(item.getId());
                        task.setCallback((nbt, success) ->
						{
                            if(success)
                            {
                                items.removeItem(index);
                            }
                        });
                        TaskManager.sendTask(task);
                    }
                });
				dialog.setNegativeText("Cancel");
				dialog.setNegativeListener((c12, mouseButton12) -> dialog.close());
				ApplicationMineBay.this.openDialog(dialog);
			}
		});
		super.addComponent(btnBuy);
		
		/* Select Item Layout */
		
		layoutSelectItem = new Layout(172, 87);
		layoutSelectItem.setTitle("Add Item");
		layoutSelectItem.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
			{
				Gui.drawRect(x, y, x + width, y + 22, Color.LIGHT_GRAY.getRGB());
				Gui.drawRect(x, y + 22, x + width, y + 23, Color.DARK_GRAY.getRGB());
				mc.fontRendererObj.drawString("Select an Item...", x + 5, y + 7, Color.WHITE.getRGB(), true);
			}
		});
		
		inventory = new Inventory(5, 28);
		inventory.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				if(inventory.getSelectedSlotIndex() != -1)
				{
					ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(inventory.getSelectedSlotIndex());
					if(!stack.isEmpty())
					{
						buttonAddNext.setEnabled(true);
						selectorAmount.setMax(stack.getCount());
						selectorAmount.setNumber(stack.getCount());
					}
					else
					{
						buttonAddNext.setEnabled(false);
					}
				}
			}
		});
		layoutSelectItem.addComponent(inventory);

		buttonAddCancel = new Button(138, 4, MINEBAY_ASSESTS, 0, 12, 8, 8);
		buttonAddCancel.setToolTip("Cancel", "Go back to main page");
		buttonAddCancel.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				restoreDefaultLayout();
			}
		});
		layoutSelectItem.addComponent(buttonAddCancel);
		
		buttonAddNext = new Button(154, 4, MINEBAY_ASSESTS, 16, 12, 8, 8);
		buttonAddNext.setToolTip("Next Page", "Set price and amount");
		buttonAddNext.setEnabled(false);
		buttonAddNext.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				selectorAmount.updateButtons();
				selectorPrice.updateButtons();
				setCurrentLayout(layoutAmountAndPrice);
			}
		});
		layoutSelectItem.addComponent(buttonAddNext);
		
		
		/* Set Amount and Price */
		
		layoutAmountAndPrice = new Layout(172, 87);
		layoutAmountAndPrice.setTitle("Add Item");
		layoutAmountAndPrice.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
			{
				Gui.drawRect(x, y, x + width, y + 22, Color.LIGHT_GRAY.getRGB());
				Gui.drawRect(x, y + 22, x + width, y + 23, Color.DARK_GRAY.getRGB());
				mc.fontRendererObj.drawString("Set amount and price...", x + 5, y + 7, Color.WHITE.getRGB(), true);
				
				int offsetX = 14;
				int offsetY = 40;
				Gui.drawRect(x + offsetX, y + offsetY, x + offsetX + 38, y + offsetY + 38, Color.BLACK.getRGB());
				Gui.drawRect(x + offsetX + 1, y + offsetY + 1, x + offsetX + 37, y + offsetY + 37, Color.DARK_GRAY.getRGB());
				
				offsetX = 90;
				Gui.drawRect(x + offsetX, y + offsetY, x + offsetX + 38, y + offsetY + 38, Color.BLACK.getRGB());
				Gui.drawRect(x + offsetX + 1, y + offsetY + 1, x + offsetX + 37, y + offsetY + 37, Color.DARK_GRAY.getRGB());
				
				if(inventory.getSelectedSlotIndex() != -1)
				{
					ItemStack stack = mc.player.inventory.getStackInSlot(inventory.getSelectedSlotIndex());
					if(!stack.isEmpty())
					{
						GlStateManager.pushMatrix();
						{
							GlStateManager.translate(x + 17, y + 43, 0);
							GlStateManager.scale(2, 2, 0);
							RenderUtil.renderItem(0, 0, stack, false);
						}
						GlStateManager.popMatrix();
					}
				}
				
				GlStateManager.pushMatrix();
				{
					GlStateManager.translate(x + 92, y + 43, 0);
					GlStateManager.scale(2, 2, 0);
					RenderUtil.renderItem(0, 0, EMERALD, false);
				}
				GlStateManager.popMatrix();
			}
		});
		
		buttonAmountAndPriceBack = new Button(122, 4, MINEBAY_ASSESTS, 8, 12, 8, 8);
		buttonAmountAndPriceBack.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutSelectItem);
			}
		});
		layoutAmountAndPrice.addComponent(buttonAmountAndPriceBack);		
		
		buttonAmountAndPriceCancel = new Button(138, 4, MINEBAY_ASSESTS, 0, 12, 8, 8);
		buttonAmountAndPriceCancel.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				restoreDefaultLayout();
			}
		});
		layoutAmountAndPrice.addComponent(buttonAmountAndPriceCancel);
		
		buttonAmountAndPriceNext = new Button(154, 4, MINEBAY_ASSESTS, 16, 12, 8, 8);
		buttonAmountAndPriceNext.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutDuration);
			}
		});
		layoutAmountAndPrice.addComponent(buttonAmountAndPriceNext);	
		
		labelAmount = new Label("Amount", 16, 30);
		layoutAmountAndPrice.addComponent(labelAmount);
		
		selectorAmount = new NumberSelector(55, 42, 18);
		selectorAmount.setMax(64);
		layoutAmountAndPrice.addComponent(selectorAmount);
		
		labelPrice = new Label("Price", 96, 30);
		layoutAmountAndPrice.addComponent(labelPrice);
		
		selectorPrice = new NumberSelector(131, 42, 24);
		selectorPrice.setMax(999);
		layoutAmountAndPrice.addComponent(selectorPrice);
		
		
		/* Duration Layout */
		layoutDuration = new Layout(172, 87);
		layoutDuration.setTitle("Add Item");
		layoutDuration.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
			{
				Gui.drawRect(x, y, x + width, y + 22, Color.LIGHT_GRAY.getRGB());
				Gui.drawRect(x, y + 22, x + width, y + 23, Color.DARK_GRAY.getRGB());
				mc.fontRendererObj.drawString("Set duration...", x + 5, y + 7, Color.WHITE.getRGB(), true);
			}
		});
		
		buttonDurationBack = new Button(122, 4, MINEBAY_ASSESTS, 8, 12, 8, 8);
		buttonDurationBack.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				setCurrentLayout(layoutAmountAndPrice);
			}
		});
		layoutDuration.addComponent(buttonDurationBack);		
		
		buttonDurationCancel = new Button(138, 4, MINEBAY_ASSESTS, 0, 12, 8, 8);
		buttonDurationCancel.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				restoreDefaultLayout();
			}
		});
		layoutDuration.addComponent(buttonDurationCancel);
		
		buttonDurationAdd = new Button(154, 4, MINEBAY_ASSESTS, 24, 12, 8, 8);
		buttonDurationAdd.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(Component c, int mouseButton)
			{
				final Dialog.Confirmation dialog = new Dialog.Confirmation();
				dialog.setMessageText("Are you sure you want to auction this item?");
				dialog.setPositiveText("Yes");
				dialog.setPositiveListener((c1, mouseButton1) ->
				{
                    int ticks = (int) TimeUtil.getRealTimeToTicks(selectorHours.getNumber(), selectorMinutes.getNumber(), selectorSeconds.getNumber());
                    TaskAddAuction task = new TaskAddAuction(inventory.getSelectedSlotIndex(), selectorAmount.getNumber(), selectorPrice.getNumber(), ticks);
                    task.setCallback((nbt, success) ->
					{
                        if(success)
                        {
                            List<AuctionItem> auctionItems = AuctionManager.INSTANCE.getItems();
                            items.addItem(auctionItems.get(auctionItems.size() - 1));
                        }
                    });
                    TaskManager.sendTask(task);
                    dialog.close();
                    restoreDefaultLayout();
                });
				openDialog(dialog);
			}
		});
		layoutDuration.addComponent(buttonDurationAdd);
		
		labelHours = new Label("Hrs", 45, 30);
		layoutDuration.addComponent(labelHours);
		
		labelMinutes = new Label("Mins", 76, 30);
		layoutDuration.addComponent(labelMinutes);
		
		labelSeconds = new Label("Secs", 105, 30);
		layoutDuration.addComponent(labelSeconds);
		
		DecimalFormat format = new DecimalFormat("00");
		
		selectorHours = new NumberSelector(45, 42, 20);
		selectorHours.setMax(23);
		selectorHours.setMin(0);
		selectorHours.setFormat(format);
		layoutDuration.addComponent(selectorHours);
		
		selectorMinutes = new NumberSelector(76, 42, 20);
		selectorMinutes.setMax(59);
		selectorMinutes.setMin(0);
		selectorMinutes.setFormat(format);
		layoutDuration.addComponent(selectorMinutes);
		
		selectorSeconds = new NumberSelector(107, 42, 20);
		selectorSeconds.setMax(59);
		selectorSeconds.setMin(1);
		selectorSeconds.setFormat(format);
		layoutDuration.addComponent(selectorSeconds);

		BankUtil.getBalance((nbt, success) ->
		{
            if(success)
            {
                labelMoney.setText("$" + nbt.getInteger("balance"));
            }
        });
		
		TaskGetAuctions task = new TaskGetAuctions();
		task.setCallback((nbt, success) ->
		{
            items.removeAll();
            for(AuctionItem item : AuctionManager.INSTANCE.getItems())
            {
                items.addItem(item);
            }
        });
		TaskManager.sendTask(task);
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
