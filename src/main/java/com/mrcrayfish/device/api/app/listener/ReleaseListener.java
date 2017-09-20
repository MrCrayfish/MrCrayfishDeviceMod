package com.mrcrayfish.device.api.app.listener;

import com.mrcrayfish.device.api.app.Component;

/**
 * The release listener interface. Used for handling releasing 
 * clicks on components.
 * 
 * @author MrCrayfish
 */
public interface ReleaseListener 
{
	/**
	 * Called when a click on a component is released
	 * 
	 * @param c the component that was clicked
	 * @param mouseButton the mouse button used to click
	 */
	void onRelease(Component c, int mouseButton);
}
