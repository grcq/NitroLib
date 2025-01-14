package dev.grcq.nitrolib.spigot.processors.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Plugin {

    String name();
    String version();
    String description() default "";
    String[] authors() default {};
    String website() default "";
    String prefix() default "";

    String[] depend() default {};
    String[] softDepend() default {};
    String[] loadBefore() default {};
    Load load() default Load.POSTWORLD;

    String apiVersion() default "";

}
