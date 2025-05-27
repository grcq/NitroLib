package dev.grcq.nitrolib.core.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Event {
    @NotNull String getName();
    @Nullable String getShortName();
}
