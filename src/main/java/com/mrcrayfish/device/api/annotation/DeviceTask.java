package com.mrcrayfish.device.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: MrCrayfish
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeviceTask
{
    /**
     *
     * @return
     */
    String modId();

    /**
     *
     * @return
     */
    String taskId();

    /**
     *
     * @return
     */
    boolean debug() default false;
}
