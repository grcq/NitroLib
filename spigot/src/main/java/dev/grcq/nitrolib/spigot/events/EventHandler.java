package dev.grcq.nitrolib.spigot.events;

import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.core.utils.Util;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class EventHandler {

    private static final Map<Method, Long> debounceMap = new HashMap<>();
    private static final Listener listener = new Listener() {};

    public static void registerAll(JavaPlugin plugin) {
        registerAll(plugin.getClass(), plugin);
    }

    public static void registerAll(Class<?> mainClass, JavaPlugin plugin) {
        for (Class<?> clazz : Util.getClassesInPackage(mainClass)) {
            register(clazz, plugin);
        }
    }

    public static void register(Class<?> clazz, JavaPlugin plugin) {
        for (Method method : clazz.getDeclaredMethods()) {
            register(method, plugin);
        }
    }

    private static void register(Method method, JavaPlugin plugin) {
        if (!method.isAnnotationPresent(Event.class)) return;
        Class<?> clazz = method.getDeclaringClass();

        if (method.getParameterCount() != 1) {
            LogUtil.error("Invalid method parameter count: " + method.getName() + " in " + clazz.getName());
            return;
        }

        Class<?> eventClass = method.getParameterTypes()[0];
        if (!org.bukkit.event.Event.class.isAssignableFrom(eventClass)) {
            LogUtil.error("Invalid method parameter type: " + method.getName() + " in " + clazz.getName());
            return;
        }

        Object instance = null;
        if (!Modifier.isStatic(method.getModifiers())) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                instance = constructor.newInstance();
            } catch (Exception e) {
                LogUtil.handleException("Failed to create instance of class " + clazz.getSimpleName(), e);
                return;
            }
        }

        Event event = method.getAnnotation(Event.class);
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        Object finalInstance = instance;

        Class<? extends org.bukkit.event.Event> finalEventClass = (Class<? extends org.bukkit.event.Event>) eventClass;
        pluginManager.registerEvent(finalEventClass, listener, event.priority(), (listener, e) -> {
            if (e == null || finalInstance == null) return;
            if (e instanceof Cancellable && !event.ignoreCancelled()) {
                Cancellable cancellable = (Cancellable) e;
                if (cancellable.isCancelled()) return;
            }

            if (event.debounce() > 0) {
                long debounce = debounceMap.getOrDefault(method, 0L);
                if (System.currentTimeMillis() - debounce < (event.debounce() * 1000L)) return;
            }

            try {
                if (!method.isAccessible()) method.setAccessible(true);
                method.invoke(finalInstance, e);
            } catch (InvocationTargetException ex) {
                if (ex.getMessage() == null || ex.getMessage().equals("null")) return;

                LogUtil.handleException("Failed to invoke event method: " + method.getName(), ex);
            } catch (Exception ex) {
                LogUtil.handleException("Failed to invoke event method: " + method.getName(), ex);
            }
        }, plugin);
    }

}
