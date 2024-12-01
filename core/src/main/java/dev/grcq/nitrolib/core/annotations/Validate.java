package dev.grcq.nitrolib.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
    String regex() default "";
    String message() default "";
    boolean nullable() default false;
}
