package com.mrcrayfish.device.api.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: MrCrayfish
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeviceApplication
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
    String appId();

    /**
     *
     * @return
     */
    boolean debug() default false;
}
