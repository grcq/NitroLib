package dev.grcq.nitrolib.core.events;

public interface Cancellable {
    boolean isCancelled();
    void setCancelled(boolean cancelled);
}
