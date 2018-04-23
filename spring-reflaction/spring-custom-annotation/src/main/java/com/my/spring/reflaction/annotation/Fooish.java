package com.my.spring.reflaction.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Fooish {
    boolean cool() default true;

    String[] tags() default {"all"};
}