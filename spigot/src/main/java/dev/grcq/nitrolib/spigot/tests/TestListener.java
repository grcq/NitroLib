package dev.grcq.nitrolib.spigot.tests;

import dev.grcq.nitrolib.spigot.events.Event;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;

public class TestListener {

    @Event
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.broadcastMessage("Player " + e.getPlayer().getName() + " has joined the server!");
    }

}
