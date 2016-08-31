package com.mrcrayfish.device.api.app;

import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.device.api.app.listener.InitListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

/**
 * The Layout class is the main implementation for displaying
 * components in your application. You can have multiple layouts
 * in your application to switch interfaces during runtime.
 * <p>
 * Use {@link com.mrcrayfish.device.api.app.Application#setCurrentLayout(Layout)} 
 * inside of {@link com.mrcrayfish.device.api.app.Application#init(int, int)} 
 * to set the layout for your application. 
 * <p>
 * Check out the example applications to get a better understand of
 * how this works.
 * 
 * @author MrCrayfish
 */
public class Layout
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
	
	/**
	 * Constructor to set a custom width and height. It should be
	 * noted that the width must be in the range of 20 to 362 and 
	 * the height 20 to 164.
	 * 
	 * @param width
	 * @param height
	 */
	public Layout(int width, int height)
	{
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
	protected final void init()
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
	public final void addComponent(Component c)
	{
		if(c != null)
		{
			this.components.add(c);
			c.init(this);
		}
	}

	/**
	 * Renders the background of this layout if a {@link Background}
	 * has be set. See {@link #setBackground(Background)}.
	 * 
	 * @param gui a Gui instance
	 * @param mc a Minecraft instance
	 * @param x the starting x rendering position (left most)
	 * @param y the starting y rendering position (top most)
	 */
	public final void render(Gui gui, Minecraft mc, int x, int y)
	{
		if(background != null)
		{
			background.render(gui, mc, x, y, width, height);
		}
	}
	
	/**
	 * Sets the initialization listener for this layout.
	 * See {@link InitListener}.
	 * 
	 * @param initListener
	 */
	public final void setInitListener(InitListener initListener)
	{
		this.initListener = initListener;
	}
	
	/**
	 * Sets the background for this layout.
	 * See {@link Background}.
	 * 
	 * @param initListener
	 */
	public final void setBackground(Background background) 
	{
		this.background = background;
	}
	
	/**
	 * Clears all components in this layout
	 */
	public final void clear() 
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
		public void render(Gui gui, Minecraft mc, int x, int y, int width, int height);
	}
}
