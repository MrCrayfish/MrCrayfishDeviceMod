package com.mrcrayfish.device.api.app.listener;

import com.mrcrayfish.device.api.app.Component;

/**
 * The change listener interface. Used for handling value
 * changing in components
 *
 * @author MrCrayfish
 */
public interface ChangeListener<T>
{
    /**
     * Called when the value is changed
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    void onChange(T oldValue, T newValue);
}
