package com.mrcrayfish.device.api.app.listeners;

import com.mrcrayfish.device.api.app.Component;

public abstract class ReleaseListener 
{
	public abstract void onRelease(Component c, int mouseButton);
}
