package dev.grcq.nitrolib.core.events;

import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {

    private final Map<Class<? extends Event>, List<EventMethod>> listeners;

    public EventBus() {
        this.listeners = new HashMap<>();
    }

    public void send(Event event) {
        listeners.putIfAbsent(event.getClass(), new ArrayList<>());

        List<EventMethod> methods = listeners.get(event.getClass());
        if (methods == null) return;

        for (EventMethod method : methods) {
            if (event instanceof Cancellable && ((Cancellable) event).isCancelled() && !method.eventHandler.ignoreCancelled()) continue;

            if (method.eventHandler.async()) new Thread(() -> method.execute(event)).start();
            else method.execute(event);
        }
    }

    public void register(Object... instances) {
        for (Object instance : instances) {
            for (Method method : instance.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 1) {
                        LogUtil.error("Method '%s' in class '%s' must have exactly one parameter.", method.getName(), instance.getClass().getName());
                        continue;
                    }

                    Class<?> eventClass = parameterTypes[0];
                    if (!Event.class.isAssignableFrom(eventClass)) {
                        LogUtil.error("Method '%s' in class '%s' must have a parameter of type Event.", method.getName(), instance.getClass().getName());
                        continue;
                    }

                    EventMethod eventMethod = new EventMethod(method, instance, method.getAnnotation(EventHandler.class));
                    List<EventMethod> methods = listeners.getOrDefault(eventClass, new ArrayList<>());
                    methods.add(eventMethod);
                }
            }
        }

        updateListeners();
    }

    public void unregister(Object instance) {
        for (List<EventMethod> methods : listeners.values()) {
            methods.removeIf(eventMethod -> eventMethod.instance.equals(instance));
        }
    }

    private void updateListeners() {
        for (Class<? extends Event> eventClass : listeners.keySet()) {
            listeners.get(eventClass)
                    .sort((o1, o2) -> {
                        int priority1 = o1.eventHandler.priority().getValue();
                        int priority2 = o2.eventHandler.priority().getValue();
                        return Integer.compare(priority1, priority2);
                    });
        }
    }

    @AllArgsConstructor
    protected static class EventMethod {

        private final Method method;
        private final Object instance;
        private final EventHandler eventHandler;

        public void execute(Event event) {
            try {
                method.invoke(instance, event);
            } catch (Exception e) {
                LogUtil.handleException("Failed to execute method '%s' for event %s.", e, 10, method.getName(), event.getName());
            }
        }
    }
}
