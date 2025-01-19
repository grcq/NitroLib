package dev.grcq.nitrolib.spigot.command.parameters.impl;

import dev.grcq.nitrolib.spigot.command.parameters.TypeParameter;
import dev.grcq.nitrolib.spigot.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class WorldType implements TypeParameter<World> {

    public static String NOT_FOUND = "&cError: The world '&e%s&c' was not found.";

    @Override
    public @Nullable World parse(CommandSender sender, String[] flags, String arg) {
        if (sender instanceof Player && arg.equalsIgnoreCase("@s")) {
            return ((Player) sender).getWorld();
        }

        World world = Bukkit.getWorld(arg);
        if (world != null) return world;

        sender.sendMessage(ChatUtil.format(NOT_FOUND, arg));
        return null;
    }
}
