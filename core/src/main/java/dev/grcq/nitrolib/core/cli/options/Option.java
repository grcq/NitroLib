package dev.grcq.nitrolib.core.cli.options;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {

    /**
     * The full names of an option, not required if shortNames is provided
     * Do not include the leading dashes as they will be added automatically
     * Example: names = {"name", "names"} will be converted to {"--name", "--names"}
     * @return The names of the option
     */
    String[] names();

    /**
     * The short names of an option, not required if names is provided
     * Do not include the leading dashes as they will be added automatically
     * Example: shortNames = {"n", "N"} will be converted to {"-n", "-N"}
     * @return The short names of the option
     */
    String[] shortNames();

    /**
     * The description of the option
     * @return The description of the option
     */
    String description() default "";

    /**
     * This decides what type the value of the option should be.
     * @see OptionValue
     * @return The value type of the option
     */
    OptionValue value() default OptionValue.STRING;
}
