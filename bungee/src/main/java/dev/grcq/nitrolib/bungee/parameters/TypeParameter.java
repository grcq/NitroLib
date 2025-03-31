package dev.grcq.nitrolib.bungee.parameters;

import net.md_5.bungee.api.CommandSender;
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
