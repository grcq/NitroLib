package dev.grcq.nitrolib.core.config;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    /**
     * File name of the configuration file
     * @return The file name of the configuration file
     */
    @NotNull String value();

    /**
     * Whether the configuration file should be created if it does not exist
     * @return Whether the configuration file should be created if it does not exist
     */
    boolean createIfNotExists() default false;
}
