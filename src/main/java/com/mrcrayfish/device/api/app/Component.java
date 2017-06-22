package com.mrcrayfish.device.api.app;

import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public abstract class Component extends Gui
{
	/**
	 * The default components textures
	 */
	public static final ResourceLocation COMPONENTS_GUI = new ResourceLocation("cdm:textures/gui/components.png");

	/**
	 * The raw x position of the component. This is not relative to the application.
	 */
	public int xPosition;
	
	/**
	 * The raw y position of the component.  This is not relative to the application.
	 */
	public int yPosition;
	
	/**
	 * The relative x position from the left.
	 */
	protected int left;
	
	/**
	 * The relative y position from the top.
	 */
	protected int top;
	
	/**
	 * Is the component enabled
	 */
	protected boolean enabled = true;
	
	/**
	 * Is the component visible
	 */
	protected boolean visible = true;
	
	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_RIGHT = 1;
	public static final int ALIGN_CENTER = 2;
	
	/**
	 * The default constructor for a component. For your component to
	 * be laid out correctly, make sure you use the x and y parameters
	 * from {@link Application#init(int x, int y)} and pass them into the
	 * x and y arguments of this constructor. 
	 * <p>
	 * Laying out the components is a simple relative positioning. So for left (x position),
	 * specific how many pixels from the left of the application window you want
	 * it to be positioned at. The top is the same, but obviously from the top (y position).
	 * 
	 * @param x the application x position (from {@link Application#init(int x, int y)}.
	 * @param y the application y position (from {@link Application#init(int x, int y)}.
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 */
	public Component(int left, int top) 
	{
		this.left = left;
		this.top = top;
	}
	
	/***
	 * Called when this component is initialized. You can add
	 * sub-components through this method. Use {@link Layout#addComponent(Component)}
	 * @param layout
	 */
	public void init(Layout layout) {}
	
	/**
	 * Called when the game ticks
	 */
	public void handleTick() {}

	/**
	 * The main render loop. This is where you draw your component.
	 * 
	 * @param laptop a Laptop instance
	 * @param mc a Minecraft instance
	 * @param mouseX the current x position of the mouse
	 * @param mouseY the current y position of the mouse
	 * @param windowActive if the window is active (at front)
	 * @param partialTicks percentage passed in-between two ticks
	 */
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {}
	
	/**
	 * The overlay render loop. Renders over the top of the main render
	 * loop.
	 * 
	 * @param laptop a Laptop instance
	 * @param mc a Minecraft instance
	 * @param mouseX the current x position of the mouse
	 * @param mouseY the current y position of the mouse
	 * @param windowActive if the window is active (at front)
	 */
	public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {}

	/**
	 * Called when you mouse button has been pressed. You have to do
	 * your own checking to test if it was within the component's
	 * bounds.
	 * 
	 * @param mouseX the current x position of the mouse
	 * @param mouseY the current y position of the mouse
	 * @param mouseButton the clicked mouse button
	 */
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {}
	
	/** 
	 * Called when a key is typed from your keyboard.
	 * 
	 * @param character the typed character
	 * @param code the typed character code
	 */
	public void handleKeyTyped(char character, int code) {}
	
	/** 
	 * Called when a key is released from your keyboard.
	 * 
	 * @param character the released character
	 * @param code the released character code
	 */
	public void handleKeyReleased(char character, int code) {}
	
	/**
	 * Called when you drag the mouse with a button pressed down.
	 * 
	 * @param mouseX the current x position of the mouse
	 * @param mouseY the current y position of the mouse
	 * @param mouseButton the pressed mouse button
	 */
	public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {}
	
	/**
	 * Called when you release the currently pressed mouse button. You have to do
	 * your own checking to test if it was within the component's
	 * bounds.
	 * 
	 * @param mouseX the x position of the release
	 * @param mouseY the y position of the release
	 * @param mouseButton the button that was released
	 */
	public void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {}
	
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction) {}
	
	/**
	 * This method should be ignored. Used for the core.
	 * Will probably be removed in the future.
	 */
	public void updateComponents(int x, int y) 
	{
		this.xPosition = x + left;
		this.yPosition = y + top;
	}
	
	/**
	 * Sets whether this component is enabled. You should respect
	 * this value if you create your own custom components.
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}
	
	/**
	 * Sets whether this component is visible. You should respect
	 * this value if you create your own custom components.
	 * 
	 * @param enabled
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
}
