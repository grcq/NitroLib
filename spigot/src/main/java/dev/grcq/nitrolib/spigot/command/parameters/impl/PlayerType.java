package dev.grcq.nitrolib.spigot.command.parameters.impl;

import dev.grcq.nitrolib.spigot.command.parameters.TypeParameter;
import dev.grcq.nitrolib.spigot.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerType implements TypeParameter<Player> {

    public static final String NOT_FOUND = "&cError: The player '&e%s&c' does not exist.";

    @Override
    public Player parse(CommandSender sender, String[] flags, String arg) {
        if (arg.equalsIgnoreCase("@s") && sender instanceof Player) {
            return (Player) sender;
        }

        Player player = Bukkit.getPlayerExact(arg);
        if (player != null) return player;

        sender.sendMessage(ChatUtil.format(NOT_FOUND, arg));
        return null;
    }

    @Override
    public @NotNull List<String> tabComplete(CommandSender sender, String[] flags, String arg) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (sender instanceof Player) players = players.stream().filter(p -> {
            Player player = (Player) sender;
            return player.getName().equalsIgnoreCase(p.getName()) || player.canSee(p);
        }).collect(Collectors.toList());

        return players.stream().map(Player::getName).collect(Collectors.toList());
    }
}
