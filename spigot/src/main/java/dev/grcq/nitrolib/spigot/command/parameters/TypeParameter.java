package dev.grcq.nitrolib.spigot.command.parameters;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface TypeParameter<T> {

    @Nullable T parse(CommandSender sender, String[] flags, String arg);

    default @NotNull List<String> tabComplete(CommandSender sender, String[] flags, String arg) {
        return new ArrayList<>();
    }

}
