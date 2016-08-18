package com.mrcrayfish.device.api.app;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

/**
 * The abstract base class for creating applications.
 * 
 * @author MrCrayfish
 */
public abstract class Application 
{
	protected ResourceLocation icon;
	protected int u, v;
	
	private final String APP_ID;
	private final String DISPLAY_NAME;
	
	private int width, height;
	
	private final Layout defaultLayout;
	private Layout currentLayout;
	
	/** If set to true, will update NBT data for Application */
	private boolean needsDataUpdate = false;
	
	/* If set to true, will update layout */
	boolean pendingLayoutUpdate = false;
	
	public Application(String appId, String displayName) 
	{
		this(appId, displayName, null, 0, 0);
	}
	
	public Application(String appId, String displayName, ResourceLocation icon, int iconU, int iconV) 
	{
		this.APP_ID = appId;
		this.DISPLAY_NAME = displayName;
		this.icon = icon;
		this.u = iconU;
		this.v = iconV;
		this.defaultLayout = new Layout();
	}
	
	/**
	 * Adds a component to the default layout. Don't get this confused
	 * with your custom layout. You should use {@link com.mrcrayfish.device.api.app.Layout#addComponent(Component)}
	 * instead.
	 * 
	 * @param c the component to add to the default layout
	 */
	protected final void addComponent(Component c)
	{
		if(c != null)
		{
			defaultLayout.addComponent(c);
			c.init(defaultLayout);
		}
	}
	
	/**
	 * Sets the current layout of the application.
	 * 
	 * @param layout
	 */
	protected final void setCurrentLayout(Layout layout)
	{
		this.currentLayout = layout;
		this.width = layout.width;
		this.height = layout.height;
		this.pendingLayoutUpdate = true;
		this.currentLayout.init();
	}
	
	/**
	 * Gets the current layout being displayed
	 * 
	 * @return the current layout
	 */
	protected final Layout getCurrentLayout() 
	{
		return currentLayout;
	}
	
	/**
	 * Restores the current layout to the default layout
	 */
	protected final void restoreDefaultLayout()
	{
		this.setCurrentLayout(defaultLayout);
	}
	
	/**
	 * The default initialization method. Clears any components in the
	 * default layout and sets it as the current layout. If you override
	 * this method and are using the default layout, make sure you call
	 * it using <code>super.init(x, y)</code>
	 * <p>
	 * The parameters passed are the x and y location of the top left corner
	 * or your application window.
	 * 
	 * @param x the starting x position
	 * @param y the starting y position
	 */
	public void init(int x, int y)
	{
		this.defaultLayout.clear();
		this.setCurrentLayout(defaultLayout);
	}
	
	//TODO for public api, make default visibilty
	public void onTick() 
	{
		for(Component c : currentLayout.components)
		{
			c.handleTick();
		}
	}
	
	//TODO Remove laptop instance
	/**
	 * 
	 * 
	 * @param laptop
	 * @param mc
	 * @param x
	 * @param y
	 * @param mouseX
	 * @param mouseY
	 * @param active
	 * @param partialTicks
	 */
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
	{
		currentLayout.render(laptop, mc, x, y);
		
		for(Component c : currentLayout.components)
		{
			c.render(laptop, mc, mouseX, mouseY, active, partialTicks);
		}
		
		for(Component c : currentLayout.components)
		{
			c.renderOverlay(laptop, mc, mouseX, mouseY, active);
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
	}
	
	/**
	 * Called when you press a mouse button. Note if you override,
	 * make sure you call this super method.
	 * 
	 * @param mouseX the current x position of the mouse
	 * @param mouseY the current y position of the mouse
	 * @param mouseButton the clicked mouse button
	 */
	public void handleClick(int mouseX, int mouseY, int mouseButton) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleClick(mouseX, mouseY, mouseButton);
		}
	}
	
	/**
	 * Called when you drag the mouse with a button pressed down
	 * Note if you override, make sure you call this super method.
	 * 
	 * @param mouseX the current x position of the mouse
	 * @param mouseY the current y position of the mouse
	 * @param mouseButton the pressed mouse button
	 */
	public void handleDrag(int mouseX, int mouseY, int mouseButton)
	{
		for(Component c : currentLayout.components)
		{
			c.handleDrag(mouseX, mouseY, mouseButton);
		}
	}
	
	/**
	 * Called when you release the currently pressed mouse button.
	 * Note if you override, make sure you call this super method.
	 * 
	 * @param mouseX the x position of the release
	 * @param mouseY the y position of the release
	 * @param mouseButton the button that was released
	 */
	public void handleRelease(int mouseX, int mouseY, int mouseButton) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleRelease(mouseX, mouseY, mouseButton);
		}
	}
	
	/** 
	 * Called when a key is typed from your keyboard.
	 * Note if you override, make sure you call this super method.
	 * 
	 * @param character the typed character
	 * @param code the typed character code
	 */
	public void handleKeyTyped(char character, int code) 
	{
		for(Component c : currentLayout.components)
		{
			c.handleKeyTyped(character, code);
		}
	}
	
	//TODO: Remove from here and put into core
	/**
	 * Updates the components of the current layout to adjust
	 * to new window position. There is really be no reason to
	 * call this method.
	 * 
	 * @param x
	 * @param y
	 */
	public final void updateComponents(int x, int y)
	{
		for(Component c : currentLayout.components)
		{
			c.updateComponents(x, y);
		}
	}
	
	/**
	 * Called when the application is closed
	 */
	public void onClose()
	{
		restoreDefaultLayout();
		defaultLayout.components.clear();
		currentLayout = null;
	}

	/**
	 * Called when you first load up your application. Allows you to
	 * read any stored data you have saved. Only called if you have 
	 * saved data.
	 * 
	 * @param tagCompound the tag compound where you saved data is
	 */
	public abstract void load(NBTTagCompound tagCompound);
	
	/**
	 * Allows you to save data from your application. This is only
	 * called if {@link #isDirty()} returns true. You can mark your
	 * application as dirty by calling {@link #markDirty()}.
	 * 
	 * @param tagCompound the tag compound to save your data to
	 */
	public abstract void save(NBTTagCompound tagCompound);
	
	/**
	 * Sets the defaults layout width. It should be noted that 
	 * the width must be within 20 to 362.
	 * 
	 * @param width the width
	 */
	protected final void setDefaultWidth(int width)
	{
		if(width < 20)
			throw new IllegalArgumentException("Width must be larger than 20");
		this.defaultLayout.width = width;
	}
	
	/**
	 * Sets the defaults layout height. It should be noted that 
	 * the height must be within 20 to 164.
	 * 
	 * @param height the height
	 */
	protected final void setDefaultHeight(int height)
	{
		if(height < 20)
			throw new IllegalArgumentException("Height must be larger than 20");
		this.defaultLayout.height = height;
	}
	
	/**
	 * Marks that data in this application has changed and needs to
	 * be saved. You must call this otherwise your data wont be saved!
	 */
	protected final void markDirty() 
	{
		needsDataUpdate = true;
	}
	
	/**
	 * Gets if this application is pending for it's data
	 * to be saved.
	 * 
	 * @return if currently requiring data to be saved
	 */
	public final boolean isDirty() 
	{
		return needsDataUpdate;
	}
	
	/**
	 * Cancels the data saving for this application
	 */
	public final void clean() 
	{
		needsDataUpdate = false;
	}
	
	/**
	 * Gets if a layout is currently pending a layout update
	 * 
	 * @return if pending layout update
	 */
	public final boolean isPendingLayoutUpdate()
	{
		return pendingLayoutUpdate;
	}
	
	/**
	 * Clears the pending layout update
	 */
	public final void clearPendingLayout() 
	{
		this.pendingLayoutUpdate = false;
	}
	
	/**
	 * Gets the id of this application
	 * 
	 * @return the applicaiton id
	 */
	public final String getID()
	{
		return APP_ID;
	}
	
	/**
	 * Gets the name that is displayed in the task bar.
	 * 
	 * @return the display name
	 */
	public final String getDisplayName()
	{
		return DISPLAY_NAME;
	}
	
	/**
	 * Gets the width of this application including the
	 * border.
	 * 
	 * @return the height
	 */
	public final int getWidth() 
	{
		return width;
	}
	
	/**
	 * Gets the height of this application including the
	 * title bar.
	 * 
	 * @return the height
	 */
	public final int getHeight() 
	{
		return height;
	}
	
	/**
	 * Gets the text in the title bar of the application.
	 * You can change the text by setting a custom title
	 * for your layout. See {@link Layout#setTitle}.
	 * 
	 * @return The display name
	 */
	public String getTitle()
	{
		return DISPLAY_NAME;
	}
	
	/**
	 * Gets the resource location the icon is located in
	 * 
	 * @return the icon resource location
	 */
	public ResourceLocation getIcon()
	{
		return icon;
	}
	
	/**
	 * Gets the u location of the icon
	 * 
	 * @return the u position
	 */
	public int getIconU()
	{
		return u;
	}
	
	/**
	 * Gets the v location of the icon
	 * 
	 * @return the v position
	 */
	public int getIconV()
	{
		return v;
	}
	
	/**
	 * Check if an application is equal to another.
	 * Checking the ID is sufficient as they should be unique.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		if(!(obj instanceof Application)) return false;
		Application app = (Application) obj;
		return app.getID().equals(this.getID());
	}
}
