package dev.grcq.nitrolib.core.inject;

import dev.grcq.nitrolib.core.annotations.Inject;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.core.utils.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class InjectHandler {

    private static final Map<Class<?>, Object> injectables;

    static {
        injectables = new HashMap<>();
    }

    private final Class<?> mainClass;

    public InjectHandler(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    public static <T> void register(Class<T> clazz, T instance) {
        injectables.put(clazz, instance);
    }

    public static <T> T get(Class<T> clazz) {
        return clazz.cast(injectables.get(clazz));
    }

    public static boolean contains(Class<?> clazz) {
        return injectables.containsKey(clazz);
    }

    public void inject() {
        for (Class<?> clazz : Util.getClassesInPackageWithClassLoader(mainClass.getPackage().getName())) {
            boolean hasInject = false;
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Inject.class)) continue;
                if (Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Object instance = get(field.getType());
                    if (instance == null) {
                        LogUtil.warn("Failed to inject field " + field.getName());
                        LogUtil.verbose("No instance found for class " + field.getType().getName() + ", is it registered?");
                        continue;
                    }

                    try {
                        field.set(null, instance);
                    } catch (IllegalAccessException e) {
                        LogUtil.error("Failed to inject field " + field.getName());
                    }
                    continue;
                }

                hasInject = true;
            }
            if (!hasInject) continue;

            // TODO: inject inside constructors
        }
    }
}
