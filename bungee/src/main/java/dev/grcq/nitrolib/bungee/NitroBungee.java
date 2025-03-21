package dev.grcq.nitrolib.bungee;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Plugin;

@RequiredArgsConstructor
public class NitroBungee {

    private final Plugin source;

    public void enable() {
        enable(true);
    }

    public void enable(Class<?> mainClass) {
        enable(mainClass, true);
    }

    public void enable(boolean initCore) {
        enable(initCore, new String[0]);
    }

    public void enable(String[] args) {
        enable(true, args);
    }

    public void enable(Class<?> mainClass, String[] args) {
        enable(mainClass, true, args);
    }

    public void enable(Class<?> mainClass, boolean initCore) {
        enable(mainClass, initCore, new String[0]);
    }

    public void enable(boolean initCore, String[] args) {
        enable(source.getClass(), initCore, args);
    }

    public void enable(Class<?> mainClass, boolean initCore, String[] args) {

    }

}
