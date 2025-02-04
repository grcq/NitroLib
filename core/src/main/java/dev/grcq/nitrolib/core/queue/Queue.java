package dev.grcq.nitrolib.core.queue;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Queue<T> {

    private final List<T> queue;
    private final int maxSize;
    private final Consumer<T> executor;
    @Nullable
    private final Function<T, Boolean> checker;

    protected Queue(int size, Consumer<T> executor, @Nullable Function<T, Boolean> checker) {
        this.queue = new ArrayList<>();
        this.maxSize = size;
        this.executor = executor;
        this.checker = checker;
    }

    public Queue(int size, Consumer<T> executor) {
        this(size, executor, null);
    }

    public Queue(Consumer<T> executor) {
        this(-1, executor, null);
    }

    public Queue(Consumer<T> executor, @Nullable Function<T, Boolean> checker) {
        this(-1, executor, checker);
    }

    public void add(T object) {
        this.queue.add(object);
    }

    public void remove(T object) {
        this.queue.remove(object);
    }

    public ImmutableSet<T> getQueue() {
        return ImmutableSet.copyOf(queue);
    }

    public boolean inQueue(T object) {
        return queue.contains(object);
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean isFull() {
        return maxSize != -1 && queue.size() >= maxSize;
    }

    @Nullable
    public T next() {
        if (!check()) return null;

        T t = queue.remove(0);
        executor.accept(t);
        return t;
    }

    public boolean check() {
        if (isEmpty()) return false;
        if (checker == null) return true;

        T first = queue.get(0);
        return checker.apply(first);
    }

}
