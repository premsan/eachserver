package com.eachserver.application.entityaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityActionMapping {

    Class<?> entity();

    int priority() default Integer.MAX_VALUE;
}
