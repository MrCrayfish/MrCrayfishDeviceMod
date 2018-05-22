package com.mrcrayfish.device.api.app.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface App {
    String modId();
    String appId();
    boolean isDebug() default false;
    boolean isSystemApp() default false;
}
