package dev.grcq.nitrolib.core.addon;

public interface AddonEntryPoint<T> {

    void onEnable(T instance, ClassLoader classLoader);
    void onDisable();

}
