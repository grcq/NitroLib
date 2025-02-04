package dev.grcq.nitrolib.core.queue;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class QueueHandler {

    private final Map<String, Queue<?>> queues;

    public QueueHandler() {
        this.queues = new HashMap<>();
    }

    public <T> Queue<T> create(String name, int size, Consumer<T> consumer, @Nullable Function<T, Boolean> checker) {
        Queue<T> queue;
        if (queues.containsKey(name)) queue = (Queue<T>) queues.get(name);
        else queues.put(name, queue = new Queue<>(size, consumer, checker));

        return queue;
    }

    public <T> Queue<T> create(String name, int size, Consumer<T> consumer) {
        return create(name, size, consumer, null);
    }

    public <T> Queue<T> create(String name, Consumer<T> consumer) {
        return create(name, -1, consumer, null);
    }

    public <T> Queue<T> get(String name) {
        return (Queue<T>) queues.get(name);
    }

    public boolean exists(String name) {
        return queues.containsKey(name);
    }

    public void destroy(String name) {
        queues.remove(name);
    }

    public void destroyAll() {
        queues.clear();
    }

    public <T> T next(String name) {
        return (T) queues.get(name).next();
    }
}
