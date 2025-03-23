package dev.grcq.nitrolib.core.messaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Packet {
    /**
     * The packet will search for the method with this identifier.
     * @return the identifier of the packet
     */
    String value();

    /**
     * @return if the packet is async or not
     */
    boolean async() default false;
}
