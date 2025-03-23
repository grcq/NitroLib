package dev.grcq.nitrolib.spigot.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * The first value is the command name, the rest are aliases.
     * @return The command name and aliases.
     */
    String[] value();

    String description() default "";
    String permission() default "";
    String usage() default "";
    String example() default "";

    boolean hidden() default false;
    boolean async() default false;
    @Deprecated
    boolean baseExecutor() default false;

    int cooldown() default 0;
    String bypassCooldownPermission() default "";

}
