package dev.grcq.nitrolib.core.annotations.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeField {
    String value();
    boolean nullable() default true;
    boolean ignoreRootPath() default true;
}
