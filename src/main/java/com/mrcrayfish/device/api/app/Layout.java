package com.mrcrayfish.device.api.app;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.api.app.listener.InitListener;
import com.mrcrayfish.device.core.Laptop;

import com.mrcrayfish.device.util.GLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

/**
 * The Layout class is the main implementation for displaying
 * components in your application. You can have multiple layouts
 * in your application to switch interfaces during runtime.
 * <p>
 * Use {@link com.mrcrayfish.device.api.app.Application#setCurrentLayout(Layout)} 
 * inside of {@link com.mrcrayfish.device.api.app.Application#init()}
 * to set the current layout for your application.
 * <p>
 * Check out the example applications to get a better understand of
 * how this works.
 * 
 * @author MrCrayfish
 */
public class Layout extends Component
{
	/**
	 * The list of components in the layout
	 */
	public List<Component> components;
	
	/**
	 * The width of the layout
	 */
	public int width;
	
	/**
	 * The height of the layout
	 */
	public int height;
	
	private String title;
	
	private InitListener initListener;
	private Background background;
	
	/**
	 * Default constructor. Initializes a layout with a width of
	 * 200 and a height of 100. Use the alternate constructor to
	 * set a custom width and height.
	 */
	public Layout() 
	{
		this(200, 100);
	}
	
	public Layout(int width, int height)
	{
		this(0, 0, width, height);

		if(width < 13)
			throw new IllegalArgumentException("Width can not be less than 13 wide");

		if(height < 1)
			throw new IllegalArgumentException("Height can not be less than 1 tall");
		
		this.components = new ArrayList<Component>();
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Constructor to set a custom width and height. It should be
	 * noted that the width must be in the range of 20 to 362 and 
	 * the height 20 to 164.
	 * 
	 * @param width
	 * @param height
	 */
	public Layout(int left, int top, int width, int height)
	{
		super(left, top);

		if(width < 13)
			throw new IllegalArgumentException("Width can not be less than 13 wide");

		if(height < 1)
			throw new IllegalArgumentException("Height can not be less than 1 tall");
		
		this.components = new ArrayList<Component>();
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Called on the initialization of the layout. Caused by 
	 * {@link Application#setCurrentLayout(Layout)}. Will
	 * trigger on initialization listener if set. 
	 * See {@link #setInitListener(InitListener)}
	 */
	public void init()
	{
		if(initListener != null)
		{
			initListener.onInit();
		}
		handleOnLoad();
	}
	
	/**
	 * Adds a component to this layout and initializes it.
	 * 
	 * @param c the component
	 */
	public void addComponent(Component c)
	{
		if(c != null)
		{
			this.components.add(c);
			c.init(this);
		}
	}
	
	@Override
	public void init(Layout layout) {}

	@Override
	public void handleOnLoad()
	{
		for(Component c : components)
		{
			c.handleOnLoad();
		}
	}

	@Override
	public void handleTick()
	{
		for(Component c : components)
		{
			c.handleTick();
		}
	}

	/**
	 * Renders the background of this layout if a {@link Background}
	 * has be set. See {@link #setBackground(Background)}.
	 * 
	 * @param laptop a Gui instance
	 * @param mc a Minecraft instance
	 * @param x the starting x rendering position (left most)
	 * @param y the starting y rendering position (top most)
	 */
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		if(!this.visible)
			return;

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GLHelper.scissor(x, y, width, height);

		if(background != null)
		{
			background.render(laptop, mc, x, y, width, height, mouseX, mouseY, windowActive);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F);
		for(Component c : components)
		{
			GlStateManager.disableDepth();
			c.render(laptop, mc, c.xPosition, c.yPosition, mouseX, mouseY, windowActive, partialTicks);
		}

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	@Override
	public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive)
	{
		for(Component c : components)
		{
			c.renderOverlay(laptop, mc, mouseX, mouseY, windowActive);
		}
	}
	
	@Override
	public void handleKeyTyped(char character, int code)
	{
		for(Component c : components)
		{
			c.handleKeyTyped(character, code);
		}
	}
	
	@Override
	public void handleKeyReleased(char character, int code)
	{
		for(Component c : components)
		{
			c.handleKeyReleased(character, code);
		}
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		for(Component c : components)
		{
			c.handleMouseClick(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
	{
		for(Component c : components)
		{
			c.handleMouseDrag(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
	{
		for(Component c : components)
		{
			c.handleMouseRelease(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction)
	{
		for(Component c : components)
		{
			c.handleMouseScroll(mouseX, mouseY, direction);
		}
	}
	
	@Override
	public void updateComponents(int x, int y)
	{
		super.updateComponents(x, y);
		for(Component c : components)
		{
			c.updateComponents(x + left, y + top);
		}
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		for(Component c : components)
		{
			c.setEnabled(enabled);
		}
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		for(Component c : components)
		{
			c.setVisible(visible);
		}
	}
	
	/**
	 * Sets the initialization listener for this layout.
	 * See {@link InitListener}.
	 * 
	 * @param initListener
	 */
	public void setInitListener(InitListener initListener)
	{
		this.initListener = initListener;
	}
	
	/**
	 * Sets the background for this layout.
	 * See {@link Background}.
	 * 
	 * @param background the background
	 */
	public void setBackground(Background background) 
	{
		this.background = background;
	}
	
	/**
	 * Clears all components in this layout
	 */
	public void clear() 
	{	
		this.components.clear();
	}
	
	public boolean hasTitle()
	{
		return title != null;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * The background interface
	 * 
	 * @author MrCrayfish
	 */
	public interface Background
	{
		/**
		 * The render method
		 * 
		 * @param gui a Gui instance
		 * @param mc A Minecraft instance
		 * @param x the starting x rendering position (left most)
		 * @param y the starting y rendering position (top most)
		 * @param width the width of the layout
		 * @param height the height of the layout
		 */
		void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive);
	}

	public static class Context extends Layout
	{
		private boolean borderVisible = true;

		public Context(int width, int height)
		{
			super(width, height);
		}

		@Override
		public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
		{
			super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
			if(borderVisible)
			{
				drawHorizontalLine(x, x + width - 1, y, Color.BLACK.getRGB());
				drawHorizontalLine(x, x + width - 1, y + height - 1, Color.BLACK.getRGB());
				drawVerticalLine(x, y, y + height - 1, Color.BLACK.getRGB());
				drawVerticalLine(x + width - 1, y, y + height - 1, Color.BLACK.getRGB());
			}
		}

		public void setBorderVisible(boolean visible)
		{
			this.borderVisible = visible;
		}
	}
}
