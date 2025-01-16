package dev.grcq.nitrolib.spigot.command.parameters.impl;

import dev.grcq.nitrolib.spigot.command.parameters.TypeParameter;
import dev.grcq.nitrolib.spigot.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class IntegerType implements TypeParameter<Integer> {

    public static final String INVALID = "&cError: '&e%s&c' is not a valid number.";

    @Override
    public @Nullable Integer parse(CommandSender sender, String[] flags, String arg) {
        if (!arg.contains("e")) {
            try {
                return Integer.parseInt(arg);
            } catch (NumberFormatException ignored) {}
        }

        sender.sendMessage(ChatUtil.format(INVALID, arg));
        return null;
    }
}
