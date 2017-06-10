package com.mrcrayfish.device.api.app.listener;

import com.mrcrayfish.device.api.app.Component;

/**
 * The response listener interface. Used for handling responses
 * from components. The generic is the returned value.
 *
 * @author MrCrayfish
 */
public interface ResponseListener<E>
{
    /**
     * Called when a response is thrown
     *
     * @param success if the executing task was successful
     */
    public void onResponse(boolean success, E e);
}
