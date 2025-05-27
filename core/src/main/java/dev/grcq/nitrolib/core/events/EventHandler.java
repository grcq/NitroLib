package dev.grcq.nitrolib.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    boolean ignoreCancelled() default false;
    boolean async() default false;
    EventPriority priority() default EventPriority.NORMAL;

    @Getter
    @AllArgsConstructor
    enum EventPriority {
        LOWEST,
        LOW,
        NORMAL,
        HIGH,
        HIGHEST,
        MONITOR;

        private final int value;
        EventPriority() {
            this.value = ordinal();
        }
    }
}
