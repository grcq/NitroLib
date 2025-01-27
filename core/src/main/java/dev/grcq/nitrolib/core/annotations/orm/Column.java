package dev.grcq.nitrolib.core.annotations.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * @return the name of the column, if not provided the field name will be used
     */
    String value() default "";

    /**
     * @return defines if the column is nullable
     */
    boolean nullable() default true;
}
