package dev.grcq.nitrolib.spigot.command.parameters.impl;

import dev.grcq.nitrolib.spigot.command.parameters.TypeParameter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class StringType implements TypeParameter<String> {

    @Override
    public @Nullable String parse(CommandSender sender, String[] flags, String arg) {
        return arg;
    }

}
