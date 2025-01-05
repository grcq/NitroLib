package dev.grcq.nitrolib.core.annotations.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Serializable {

    /**
     * The default path to read from in the file.
     * Example: "data.player" will read from the "player" key in the "data" object in the file.
     * If the path is empty, the file will be read the whole file.
     * @return The path to read from.
     *
     */
    String value() default "";

}
