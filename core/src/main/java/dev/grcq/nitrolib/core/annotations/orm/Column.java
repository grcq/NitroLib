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

    /**
     * This will only apply to String fields.
     * If the value is Integer.MAX_VALUE, the column will be a TEXT column only if it's a String.
     * @return defines the length of the column (ex. VARCHAR(255))
     */
    int length() default 255;
}
