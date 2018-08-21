package com.mrcrayfish.device.core;

import javax.annotation.Nullable;

import com.mrcrayfish.device.api.app.Dialog;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public abstract class Wrappable
{
	private Window window;

	/**
	 * The default initialization method. Clears any components in the default layout and sets it as the current layout. If you override this method and are using the default layout, make sure you call it using <code>super.init()</code>
	 * 
	 * @param intent
	 */
	public abstract void init(@Nullable NBTTagCompound intent);

	/**
	 * When the games ticks. Note if you override, make sure you call this super method.
	 */
	public abstract void onTick();

	/**
	 * The main render loop. Note if you override, make sure you call this super method.
	 * 
	 * @param laptop
	 *            laptop instance
	 * @param mc
	 *            a Minecraft instance
	 * @param x
	 *            the starting x position
	 * @param y
	 *            the start y position
	 * @param mouseX
	 *            the mouse position x
	 * @param mouseY
	 *            the mouse position y
	 * @param active
	 *            if the window active
	 * @param partialTicks
	 *            time passed since tick
	 */
	public abstract void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks);

	/**
	 * Called when a key is typed from your keyboard. Note if you override, make sure you call this super method.
	 * 
	 * @param character
	 *            the typed character
	 * @param code
	 *            the typed character code
	 */
	public abstract void handleKeyTyped(char character, int code);

	/**
	 * Called when a key is released from your keyboard.
	 * 
	 * @param character
	 *            the released character
	 * @param code
	 *            the released character code
	 */
	public abstract void handleKeyReleased(char character, int code);

	/**
	 * Called when you press a mouse button.
	 * 
	 * @param mouseX
	 *            the current x position of the mouse
	 * @param mouseY
	 *            the current y position of the mouse
	 * @param mouseButton
	 *            the clicked mouse button
	 */
	public abstract void handleMouseClick(int mouseX, int mouseY, int mouseButton);

	/**
	 * Called when you drag the mouse with a button pressed down.
	 * 
	 * @param mouseX
	 *            the current x position of the mouse
	 * @param mouseY
	 *            the current y position of the mouse
	 * @param mouseButton
	 *            the pressed mouse button
	 */
	public abstract void handleMouseDrag(int mouseX, int mouseY, int mouseButton);

	/**
	 * Called when you release the currently pressed mouse button.
	 * 
	 * @param mouseX
	 *            the x position of the release
	 * @param mouseY
	 *            the y position of the release
	 * @param mouseButton
	 *            the button that was released
	 */
	public abstract void handleMouseRelease(int mouseX, int mouseY, int mouseButton);

	/**
	 * Called when you scroll the wheel on your mouse.
	 * 
	 * @param mouseX
	 *            the x position of the mouse
	 * @param mouseY
	 *            the y position of the mouse
	 * @param direction
	 *            the direction of the scroll. true is up, false is down
	 */
	public abstract void handleMouseScroll(int mouseX, int mouseY, boolean direction);

	/**
	 * Called when the window attempts to be resized.
	 * 
	 * @param width
	 *            The new width for the window
	 * @param height
	 *            The new height for the window
	 * @return Whether or not the window could be resized
	 */
	public abstract boolean resize(int width, int height);

	/**
	 * Gets the text in the title bar.
	 * 
	 * @return The display name
	 */
	public abstract String getWindowTitle();

	/**
	 * Gets the width of the content (application/dialog) including the border.
	 *
	 * @return the width
	 */
	public abstract int getWidth();

	/**
	 * Gets the height of the content (application/dialog) including the title bar.
	 * 
	 * @return the height
	 */
	public abstract int getHeight();

	/**
	 * Gets the whether or not the content (application/dialog) should have the borders and title.
	 * 
	 * @return if the content should render borders and title
	 */
	public abstract boolean isDecorated();

	/**
	 * Gets the whether or not the content (application/dialog) can be resized.
	 * 
	 * @return if the content can be resized
	 */
	public abstract boolean isResizable();

	/**
	 * Marks the content's layout for updating
	 */
	public abstract void markForLayoutUpdate();

	/**
	 * Gets if this content's layout is currently pending a update
	 * 
	 * @return if pending layout update
	 */
	public abstract boolean isPendingLayoutUpdate();

	/**
	 * Clears the pending layout update for this content
	 */
	public abstract void clearPendingLayout();

	/**
	 * Updates the components of this content
	 * 
	 * @param x
	 *            the starting rendering x position (left)
	 * @param y
	 *            the starting rendering y position (top)
	 */
	public abstract void updateComponents(int x, int y);

	/**
	 * Called when this content is closed
	 */
	public void onClose()
	{
	}

	/**
	 * Called when the content is resized.
	 * 
	 * @param width
	 *            The new width of the window
	 * @param height
	 *            The new height of the window
	 */
	public void onResize(int width, int height)
	{
	}

	/**
	 * Sets the Window instance. Used by the core.
	 *
	 * @param window
	 */
	public final void setWindow(Window window)
	{
		if (window == null)
			throw new IllegalArgumentException("You can't set a null window instance");
		this.window = window;
	}

	/**
	 * Gets the Window this Application is wrapped in.
	 *
	 * @return the window
	 */
	public final Window getWindow()
	{
		return window;
	}

	public final void openDialog(Dialog dialog)
	{
		window.openDialog(dialog);
	}

}
