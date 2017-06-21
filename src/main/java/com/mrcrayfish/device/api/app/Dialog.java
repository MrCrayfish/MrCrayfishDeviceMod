package com.mrcrayfish.device.api.app;

import java.awt.Color;

import com.mrcrayfish.device.api.app.component.TextField;
import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Window;
import com.mrcrayfish.device.core.Wrappable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Dialog extends Wrappable
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
		customLayout.handleTick();
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
	{
		customLayout.render(laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
		customLayout.renderOverlay(laptop, mc, mouseX, mouseY, active);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		customLayout.handleMouseClick(mouseX, mouseY, mouseButton);
		
	}

	@Override
	public void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
	{
		customLayout.handleMouseDrag(mouseX, mouseY, mouseButton);
		
	}

	@Override
	public void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
	{
		customLayout.handleMouseRelease(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction)
	{
		customLayout.handleMouseScroll(mouseX, mouseY, direction);
	}

	@Override
	public void handleKeyTyped(char character, int code)
	{
		customLayout.handleKeyTyped(character, code);
	}
	
	@Override
	public void handleKeyReleased(char character, int code)
	{
		customLayout.handleKeyReleased(character, code);
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public String getWindowTitle()
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
		window.close();
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
				public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
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
					close();
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
				public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
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
					close();
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
					close();
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

	public static class Input extends Dialog
	{
		private String messageText = null;
		private String inputText = "";
		private String positiveText = "Ok";
		private String negativeText = "Cancel";

		private ResponseHandler<String> responseListener;

		private TextField textFieldInput;
		private Button buttonPositive;
		private Button buttonNegative;

		public Input() {}

		public Input(String messageText)
		{
			this.messageText = messageText;
		}

		@Override
		public void init()
		{
			super.init();

			int offset = 0;

			if(messageText != null)
			{
				int lines = Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(messageText, getWidth() - 10).size();
				defaultLayout.height += lines * 9 + 10;
				offset += lines * 9 + 5;
			}

			super.init();

			defaultLayout.setBackground(new Background()
			{
				@Override
				public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
				{
					gui.drawRect(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
				}
			});

			if(messageText != null)
			{
				Text message = new Text(messageText, 5, 5, getWidth() - 10);
				this.addComponent(message);
			}

			textFieldInput = new TextField(5, 5 + offset, getWidth() - 10);
			textFieldInput.setText(inputText);
			textFieldInput.setFocused(true);
			this.addComponent(textFieldInput);

			int positiveWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(positiveText);
			buttonPositive = new Button(positiveText, getWidth() - positiveWidth - 15, getHeight() - 20, positiveWidth + 10, 15);
			buttonPositive.setClickListener((c, mouseButton) ->
			{
                if(!textFieldInput.getText().isEmpty())
                {
                	boolean close = true;
                    if(responseListener != null)
                    {
                        close = responseListener.onResponse(true, textFieldInput.getText().trim());
                    }
					if(close) close();
                }
            });
			this.addComponent(buttonPositive);

			int negativeWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(negativeText);
			buttonNegative = new Button(negativeText, getWidth() - positiveWidth - negativeWidth - 15 - 15, getHeight() - 20, negativeWidth + 10, 15);
			buttonNegative.setClickListener((c, mouseButton) -> close());
			this.addComponent(buttonNegative);
		}

		/**
		 * Sets the initial text for the input text field
		 * @param inputText
		 */
		public void setInputText(@Nonnull String inputText)
		{
			if(inputText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.inputText = inputText;
		}

		/**
		 * Gets the input text field. This will be null if has not been
		 * @return
		 */
		@Nullable
		public TextField getTextFieldInput()
		{
			return textFieldInput;
		}

		/**
		 * Sets the positive button text
		 * @param positiveText
		 */
		public void setPositiveText(@Nonnull String positiveText)
		{
			if(positiveText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.positiveText = positiveText;
		}

		/**
		 * Sets the negative button text
		 *
		 * @param negativeText
		 */
		public void setNegativeText(@Nonnull String negativeText)
		{
			if(negativeText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.negativeText = negativeText;
		}

		/**
		 * Sets the response handler. The handler is called when the positive
		 * button is pressed and returns the value in the input text field. Returning
		 * true in the handler indicates that the dialog should close.
		 *
		 * @param responseListener
		 */
		public void setResponseHandler(ResponseHandler<String> responseListener)
		{
			this.responseListener = responseListener;
		}
	}

	/**
	 * The response listener interface. Used for handling responses
	 * from components. The generic is the returned value.
	 *
	 * @author MrCrayfish
	 */
	public interface ResponseHandler<E>
	{
		/**
		 * Called when a response is thrown.
		 *
		 * @param success if the executing task was successful
		 */
		boolean onResponse(boolean success, E e);
	}

}
