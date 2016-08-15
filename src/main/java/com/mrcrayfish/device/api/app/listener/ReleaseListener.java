package com.mrcrayfish.device.api.app.listener;

import com.mrcrayfish.device.api.app.Component;

public abstract class ReleaseListener 
{
	public abstract void onRelease(Component c, int mouseButton);
}
