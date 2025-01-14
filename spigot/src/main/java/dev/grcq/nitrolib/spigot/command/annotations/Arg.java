package dev.grcq.nitrolib.spigot.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {

    /**
     * @return The name of the argument.
     */
    String value();

    /**
     * @return The default value of the argument.
     */
    String def() default "";

    /**
     * This will be ignored if `def` is not empty.
     * @return Whether the argument is required or not.
     */
    boolean required() default true;

    /**
     * This will only work on strings. You can not have any other parameters after this.
     * @return A consecutive amount of arguments to merge into one.
     */
    boolean wildcard() default false;

}
