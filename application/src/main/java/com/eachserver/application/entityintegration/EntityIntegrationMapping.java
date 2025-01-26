package com.eachserver.application.entityintegration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityIntegrationMapping {

    Class<?> entity();

    int priority() default Integer.MAX_VALUE;
}
