package dev.grcq.nitrolib.core.annotations.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
    /**
     * @return defines if the column is auto incremented, only works with numeric types
     */
    boolean autoIncrement() default true;
}
