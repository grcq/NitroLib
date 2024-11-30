package dev.grcq.nitrolib.core.config;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    /**
     * File name of the configuration file
     * @return The file name of the configuration file
     */
    @NotNull String value();
}
