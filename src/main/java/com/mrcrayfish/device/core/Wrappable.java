package com.mrcrayfish.device.core;

import com.mrcrayfish.device.api.app.Layout;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public interface Wrappable
{

	/**
	 * The default initialization method. Clears any components in the default
	 * layout and sets it as the current layout. If you override this method and
	 * are using the default layout, make sure you call it using
	 * <code>super.init()</code>
	 */
	void init();

	/**
	 * When the games ticks. Note if you override, make sure you call this super
	 * method.
	 */
	void onTick();

	/**
	 * The main render loop. Note if you override, make sure you call this super
	 * method.
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
	void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks);

	/**
	 * Called when a key is typed from your keyboard. Note if you override, make
	 * sure you call this super method.
	 * 
	 * @param character
	 *            the typed character
	 * @param code
	 *            the typed character code
	 */
	void handleKeyTyped(char character, int code);

	/**
	 * Called when a key is released from your keyboard.
	 * 
	 * @param character
	 *            the released character
	 * @param code
	 *            the released character code
	 */
	void handleKeyReleased(char character, int code);

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
	void handleMouseClick(int mouseX, int mouseY, int mouseButton);

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
	void handleMouseDrag(int mouseX, int mouseY, int mouseButton);

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
	void handleMouseRelease(int mouseX, int mouseY, int mouseButton);

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
	void handleMouseScroll(int mouseX, int mouseY, boolean direction);

	/**
	 * Gets the text in the title bar.
	 * 
	 * @return The display name
	 */
	String getTitle();

	/**
	 * Gets the width of the content (application/dialog) including the border.
	 * 
	 * @return the height
	 */
	int getWidth();

	/**
	 * Gets the height of the content (application/dialog) including the title
	 * bar.
	 * 
	 * @return the height
	 */
	int getHeight();

	/**
	 * Marks the content's layout for updating
	 */
	void markForLayoutUpdate();

	/**
	 * Gets if this content's layout is currently pending a update
	 * 
	 * @return if pending layout update
	 */
	boolean isPendingLayoutUpdate();

	/**
	 * Clears the pending layout update for this content
	 */
	void clearPendingLayout();

	/**
	 * Updates the components of this content
	 * 
	 * @param x
	 *            the starting rendering x position (left)
	 * @param y
	 *            the starting rendering y position (top)
	 */
	void updateComponents(int x, int y);

	/**
	 * Called when this content is closed
	 */
	void onClose();
}
