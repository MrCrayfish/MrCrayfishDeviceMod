package com.mrcrayfish.device.api.app;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Window;
import com.mrcrayfish.device.core.Wrappable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;

public abstract class Dialog implements Wrappable
{
	private String title = "Message";
	private int width;
	private int height;

	protected final Layout defaultLayout;
	private Layout customLayout;
	
	private boolean pendingLayoutUpdate = true;
	
	private Window window;
	
	public Dialog() 
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
	public void init()
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
		customLayout.render(laptop, mc, mouseX, mouseY, active, partialTicks);

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
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		for (Component c : customLayout.components)
		{
			c.handleMouseClick(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
	{
		for (Component c : customLayout.components)
		{
			c.handleMouseDrag(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
	{
		for (Component c : customLayout.components)
		{
			c.handleMouseRelease(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction)
	{
		for (Component c : customLayout.components)
		{
			c.handleMouseScroll(mouseX, mouseY, direction);
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
	
	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public String getTitle()
	{
		return title;
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
	public void markForLayoutUpdate()
	{
		this.pendingLayoutUpdate = true;
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
		customLayout.updateComponents(x, y);
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

	public void setWindow(Window window)
	{
		this.window = window;
	}
	
	public static class Message extends Dialog
	{
		private String messageText = "";
		
		private ClickListener positiveListener;
		private Button buttonPositive;
		
		public Message(String messageText)
		{
			this.messageText = messageText;
		}
		
		@Override
		public void init()
		{
			super.init();
			
			int lines = Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(messageText, getWidth() - 10).size();
			defaultLayout.height += (lines - 1) * 9;
			
			super.init();
			
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
			
			buttonPositive = new Button("Close", getWidth() - 35, getHeight() - 20, 30, 15);
			buttonPositive.setClickListener(new ClickListener()
			{
				@Override
				public void onClick(Component c, int mouseButton)
				{
					if(positiveListener != null)
					{
						positiveListener.onClick(c, mouseButton);
					}
					else
					{
						close();
					}
				}
			});
			this.addComponent(buttonPositive);
		}
	}
	
	public static class Confirmation extends Dialog
	{
		private String messageText = "Are you sure?";
		private String positiveText = "Yes";
		private String negativeText = "No";
		
		private ClickListener positiveListener;
		private ClickListener negativeListener;
		
		private Button buttonPositive;
		private Button buttonNegative;

		@Override
		public void init()
		{
			super.init();
			
			int lines = Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(messageText, getWidth() - 10).size();
			defaultLayout.height += (lines - 1) * 9;
			
			super.init();

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
			
			buttonPositive = new Button(positiveText, getWidth() - 35, getHeight() - 20, 30, 15);
			buttonPositive.setClickListener(new ClickListener()
			{
				@Override
				public void onClick(Component c, int mouseButton)
				{
					if(positiveListener != null)
					{
						positiveListener.onClick(c, mouseButton);
					}
					else
					{
						close();
					}
				}
			});
			this.addComponent(buttonPositive);
			
			buttonNegative = new Button(negativeText, getWidth() - 70, getHeight() - 20, 30, 15);
			buttonNegative.setClickListener(new ClickListener()
			{
				@Override
				public void onClick(Component c, int mouseButton)
				{
					if(negativeListener != null)
					{
						negativeListener.onClick(c, mouseButton);
					}
					else
					{
						close();
					}
				}
			});
			this.addComponent(buttonNegative);
		}
		
		public void setPositiveButton(String positiveText, ClickListener positiveListener)
		{
			this.positiveText = positiveText;
			this.positiveListener = positiveListener;
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
