package com.mrcrayfish.device.api.app;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Window;
import com.mrcrayfish.device.core.Wrappable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;

public class Dialog implements Wrappable
{
	private String title;
	private int width;
	private int height;

	protected final Layout defaultLayout;
	private Layout customLayout;
	
	private boolean pendingLayoutUpdate = true;
	
	private Window<Application> window;
	
	private Dialog() 
	{
		this.defaultLayout = new Layout(150, 40);
	}
	
	protected final void addComponent(Component c)
	{
		if (c != null)
		{
			defaultLayout.addComponent(c);
			c.init(defaultLayout);
		}
	}
	
	protected final void setLayout(Layout layout)
	{
		this.customLayout = layout;
		this.width = layout.width;
		this.height = layout.height;
		this.pendingLayoutUpdate = true;
		this.customLayout.init();
	}
	
	@Override
	public void init(int x, int y)
	{
		this.defaultLayout.clear();
		this.setLayout(defaultLayout);
	}

	@Override
	public void onTick()
	{
		for (Component c : customLayout.components)
		{
			c.handleTick();
		}
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
	{
		customLayout.render(laptop, mc, x, y);

		for (Component c : customLayout.components)
		{
			c.render(laptop, mc, mouseX, mouseY, active, partialTicks);
		}

		for (Component c : customLayout.components)
		{
			c.renderOverlay(laptop, mc, mouseX, mouseY, active);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
	}

	@Override
	public void handleClick(int mouseX, int mouseY, int mouseButton)
	{
		for (Component c : customLayout.components)
		{
			c.handleClick(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void handleDrag(int mouseX, int mouseY, int mouseButton)
	{
		for (Component c : customLayout.components)
		{
			c.handleDrag(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void handleRelease(int mouseX, int mouseY, int mouseButton)
	{
		for (Component c : customLayout.components)
		{
			c.handleRelease(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void handleKeyTyped(char character, int code)
	{
		for (Component c : customLayout.components)
		{
			c.handleKeyTyped(character, code);
		}
	}
	
	@Override
	public void handleKeyReleased(char character, int code)
	{
		for (Component c : customLayout.components)
		{
			c.handleKeyReleased(character, code);
		}
	}

	@Override
	public String getTitle()
	{
		return "Message";
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public boolean isPendingLayoutUpdate()
	{
		return pendingLayoutUpdate;
	}

	@Override
	public void clearPendingLayout()
	{
		this.pendingLayoutUpdate = false;
	}

	@Override
	public void updateComponents(int x, int y)
	{
		for (Component c : customLayout.components)
		{
			c.updateComponents(x, y);
		}
	}

	@Override
	public void onClose()
	{
		this.customLayout = null;
	}
	
	public void close()
	{
		if(window != null)
		{
			window.closeDialog();
		}
	}
	
	public static Dialog newConfirmationDialog()
	{
		Dialog dialog = new Dialog() 
		{
			@Override
			public void init(int x, int y)
			{
				super.init(x, y);
				Label label = new Label("Are you sure?", 5, 5);
				this.addComponent(label);
			}
		};
		return dialog;
	}

	public static class Builder
	{
	public void setWindow(DialogWindow window)
	{
		this.window = window;
	}
	
	public static class DialogConfirmation extends Dialog
	{
		private String messageText = "Are you sure?";
		private String positiveText = "Yes";
		private String negativeText = "No";
		
		private ClickListener postiveListener;
		private ClickListener negativeListener;
		
		@Override
		public void init(int x, int y)
		{
			super.init(x, y);
			
			int lines = Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(messageText, getWidth() - 10).size();
			defaultLayout.height += (lines - 1) * 9;
			
			setLayout(defaultLayout);
			
			defaultLayout.setBackground(new Background()
			{
				@Override
				public void render(Gui gui, Minecraft mc, int x, int y, int width, int height)
				{
					gui.drawRect(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
				}
			});
			
			Text message = new Text(messageText, 5, 5, getWidth() - 10);
			this.addComponent(message);
			
			Button postiveButton = new Button(positiveText, getWidth() - 35, getHeight() - 20, 30, 15);
			if(postiveListener != null)
			{
				System.out.println("Setting custom");
				postiveButton.setClickListener(postiveListener);
			}
			else
			{
				System.out.println("Setting default");
				postiveButton.setClickListener(new ClickListener()
				{
					@Override
					public void onClick(Component c, int mouseButton)
					{
						close();
					}
				});
			}
			this.addComponent(postiveButton);
			
			Button negativeButton = new Button(negativeText, getWidth() - 70, getHeight() - 20, 30, 15);
			if(negativeListener != null)
			{
				negativeButton.setClickListener(negativeListener);
			}
			else
			{
				negativeButton.setClickListener(new ClickListener()
				{
					@Override
					public void onClick(Component c, int mouseButton)
					{
						close();
					}
				});
			}
			this.addComponent(negativeButton);
		}
		
		public void setPositiveButton(String positiveText, ClickListener postiveListener)
		{
			this.positiveText = positiveText;
			this.postiveListener = postiveListener;
		}
		
		public void setNegativeButton(String negativeText, ClickListener negativeListener)
		{
			this.negativeText = negativeText;
			this.negativeListener = negativeListener;
		}
		
		public void setMessageText(String messageText)
		{
			this.messageText = messageText;
		}
	}
}
