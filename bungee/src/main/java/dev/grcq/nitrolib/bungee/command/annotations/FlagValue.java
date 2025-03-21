package dev.grcq.nitrolib.bungee.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlagValue {
    String name();
    String arg();

    String def() default "";
    boolean required() default true;
}
