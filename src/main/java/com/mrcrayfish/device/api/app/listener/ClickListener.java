package com.mrcrayfish.device.api.app.listener;

import com.mrcrayfish.device.api.app.interfaces.IListener;

/**
 * The click listener interface. Used for handling clicks
 * on components.
 * 
 * @author MrCrayfish
 */
public interface ClickListener extends IListener
{
	/**
	 * Called when component is clicked
	 *
	 * @param mouseButton the mouse button used to click
	 */
	void onClick(int mouseX, int mouseY, int mouseButton);
}
