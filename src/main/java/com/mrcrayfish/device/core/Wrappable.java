package com.mrcrayfish.device.core;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public interface Wrappable {
	
	/**
	 * The default initialization method. Clears any components in the defaul
	 * layout and sets it as the current layout. If you override this method and
	 * are using the default layout, make sure you call it using
	 * <code>super.init(x, y)</code>
	 * <p>
	 * The parameters passed are the x and y location of the top left corner or
	 * your application window.
	 * 
	 * @param x
	 *            the starting x position
	 * @param y
	 *            the starting y position
	 */
	void init();

	void onTick();

	/**
	 * The main render loop. Note if you override, make
	 * sure you call this super method. 
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
	 * Called when a key is released from your keyboard. Note if you override, make
	 * sure you call this super method.
	 * 
	 * @param character
	 *            the released character
	 * @param code
	 *            the released character code
	 */
	void handleKeyReleased(char character, int code);

	/**
	 * Called when you press a mouse button. Note if you override, make sure you
	 * call this super method.
	 * 
	 * @param mouseX
	 *            the current x position of the mouse
	 * @param mouseY
	 *            the current y position of the mouse
	 * @param mouseButton
	 *            the clicked mouse button
	 */
	void handleClick(int mouseX, int mouseY, int mouseButton);

	/**
	 * Called when you drag the mouse with a button pressed down Note if you
	 * override, make sure you call this super method.
	 * 
	 * @param mouseX
	 *            the current x position of the mouse
	 * @param mouseY
	 *            the current y position of the mouse
	 * @param mouseButton
	 *            the pressed mouse button
	 */
	void handleDrag(int mouseX, int mouseY, int mouseButton);

	/**
	 * Called when you release the currently pressed mouse button. Note if you
	 * override, make sure you call this super method.
	 * 
	 * @param mouseX
	 *            the x position of the release
	 * @param mouseY
	 *            the y position of the release
	 * @param mouseButton
	 *            the button that was released
	 */
	void handleRelease(int mouseX, int mouseY, int mouseButton);

	String getTitle();

	int getWidth();

	int getHeight();

	boolean isPendingLayoutUpdate();

	void clearPendingLayout();

	void updateComponents(int x, int y);

	/**
	 * Called when the window is closed
	 */
	void onClose();
}
