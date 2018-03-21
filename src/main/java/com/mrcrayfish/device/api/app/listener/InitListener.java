package com.mrcrayfish.device.api.app.listener;

import com.mrcrayfish.device.api.app.interfaces.IListener;

/**
 * The initialization listener interface. Used for running
 * code when a layout is initialized.
 * 
 * @author MrCrayfish
 */
public interface InitListener extends IListener
{
	/**
	 * Called when a layout is set as the current layout.
	 */
	void onInit();
}
