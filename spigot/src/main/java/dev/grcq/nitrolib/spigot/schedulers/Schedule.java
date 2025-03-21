package dev.grcq.nitrolib.spigot.schedulers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {

    boolean async() default false;

    long delay() default 0L;
    long period() default 0L;

    String[] dependencies() default {};
}
