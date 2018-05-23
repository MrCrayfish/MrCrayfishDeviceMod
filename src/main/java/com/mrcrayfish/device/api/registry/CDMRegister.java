package com.mrcrayfish.device.api.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CDMRegister {
    String modId();
    String uid();
    boolean isDebug() default false;
    boolean isSystem() default false;
}