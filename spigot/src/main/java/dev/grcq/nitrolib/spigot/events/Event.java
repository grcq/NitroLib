package dev.grcq.nitrolib.spigot.events;

import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {

    /**
     * @return the priority of the event
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * If set to true, the event will be called even if it was cancelled.
     * @return whether the event should be called even if it was cancelled
     */
    boolean ignoreCancelled() default false;

    /**
     * Applying a debounce time will prevent the event from being called multiple times in a short period.
     * @return the debounce time in seconds
     */
    int debounce() default 0;
}
