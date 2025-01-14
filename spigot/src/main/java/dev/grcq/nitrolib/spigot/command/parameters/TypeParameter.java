package dev.grcq.nitrolib.spigot.command.parameters;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface TypeParameter<T> {

    T parse(CommandSender sender, String[] flags, String arg);

    List<String> tabComplete(CommandSender sender, String[] flags, String arg);

}
