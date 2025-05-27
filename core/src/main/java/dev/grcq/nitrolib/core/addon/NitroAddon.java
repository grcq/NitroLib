package dev.grcq.nitrolib.core.addon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NitroAddon {

    /**
     * The name of the addon.
     * @return the name of the addon
     */
    String name();
    /**
     * The version of the addon.
     * @return the version of the addon
     */
    String version();
    /**
     * The description of the addon.
     * @return the description of the addon
     */
    String description() default "";
    /**
     * The authors of the addon.
     * @return an array of authors of the addon
     */
    String[] authors() default {};

    /**
     * Required addons that this addon depends on, it will load after the specified addons.
     * @return an array of addon names that this addon depends on
     */
    String[] depend() default {};
    /**
     * Optional addons that this addon can work with if they are present, it will load after the specified addons if they are present.
     * @return an array of addon names that this addon can work with if they are present
     */
    String[] softDepend() default {};
    /**
     * The class that will be used to load the addon.
     * @return the class that will be used to load the addon
     */
    String[] loadBefore() default {};
}
