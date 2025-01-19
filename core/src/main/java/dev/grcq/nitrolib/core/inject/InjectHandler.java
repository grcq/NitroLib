package dev.grcq.nitrolib.core.inject;

import dev.grcq.nitrolib.core.annotations.Inject;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.core.utils.Util;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class InjectHandler {

    private static final Map<Class<?>, Object> injectables;

    static {
        injectables = new HashMap<>();
    }

    private final Class<?> mainClass;

    public InjectHandler(Class<?> mainClass) {
        this.mainClass = mainClass;
        ByteBuddyAgent.install();
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
        for (Class<?> clazz : Util.getClassesInPackage(mainClass)) {
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

            Class<?> dynamicClass = new ByteBuddy()
                    .subclass(clazz)
                    .constructor(ElementMatchers.any())
                    .intercept(SuperMethodCall.INSTANCE.andThen(Advice.to(InjectAdvice.class)))
                    .make()
                    .load(clazz.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
                    .getLoaded();
        }
    }

    static class InjectAdvice {
        @Advice.OnMethodExit
        static void inject(@Advice.This Object self) throws Exception {
            for (Field field : self.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(Inject.class)) continue;
                if (Modifier.isStatic(field.getModifiers())) continue;

                Class<?> type = field.getType();
                if (!contains(type)) {
                    LogUtil.warn("Failed to inject field " + field.getName() );
                    LogUtil.verbose("No instance found for class " + type.getName() + ", is it registered?");
                    continue;
                }

                field.setAccessible(true);
                field.set(self, get(type));
            }
        }
    }
}
