package com.mrcrayfish.device.api.app.listener;

import com.mrcrayfish.device.api.app.interfaces.IListener;

/**
 * The change listener interface. Used for handling value
 * changing in components
 *
 * @author MrCrayfish
 */
public interface ChangeListener<T> extends IListener
{
    /**
     * Called when the value is changed
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    void onChange(T oldValue, T newValue);
}
