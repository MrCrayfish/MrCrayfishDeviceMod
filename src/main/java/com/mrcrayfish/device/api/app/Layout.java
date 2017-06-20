package com.mrcrayfish.device.api.app;

import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.api.app.listener.InitListener;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

/**
 * The Layout class is the main implementation for displaying
 * components in your application. You can have multiple layouts
 * in your application to switch interfaces during runtime.
 * <p>
 * Use {@link com.mrcrayfish.device.api.app.Application#setCurrentLayout(Layout)} 
 * inside of {@link com.mrcrayfish.device.api.app.Application#init()}
 * to set the layout for your application. 
 * <p>
 * Check out the example applications to get a better understand of
 * how this works.
 * 
 * @author MrCrayfish
 */
public final class Layout extends Component
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
		if(width < 20  || height < 20)
			throw new IllegalArgumentException("Height and width of layout must be larger than 20");
		
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
		if(width < 20  || height < 20)
			throw new IllegalArgumentException("Height and width of layout must be larger than 20");
		
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
	protected void init()
	{
		if(initListener != null)
		{
			initListener.onInit();
		}
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
	public void init(Layout layout)
	{
		for(Component c : components)
		{
			c.init(layout);
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
	 * @param laptop a Laptop instance
	 * @param mc a Minecraft instance
	 * @param x the starting x rendering position (left most)
	 * @param y the starting y rendering position (top most)
	 */
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		if(background != null)
		{
			background.render(laptop, mc, x, y, width, height, mouseX, mouseY, windowActive);
		}
		for(Component c : components)
		{
			GlStateManager.disableDepth();
			c.render(laptop, mc, c.xPosition, c.yPosition, mouseX, mouseY, windowActive, partialTicks);
		}
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
	 * @param initListener
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
		public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive);
	}

}
