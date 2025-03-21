package dev.grcq.nitrolib.spigot.tests;

import com.google.common.collect.Lists;
import dev.grcq.nitrolib.spigot.command.annotations.Command;
import dev.grcq.nitrolib.spigot.schedulers.Schedule;
import dev.grcq.nitrolib.spigot.events.Event;
import dev.grcq.nitrolib.spigot.scoreboard.NitroPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class TestListener {

    private static int counter = 0;
    private static final NitroPlayerBoard board = new NitroPlayerBoard(p -> "test " + p.getName(), p -> Lists.newArrayList("line 1", "line 2", p.getName(), counter + " counter"));

    @Event
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Bukkit.broadcastMessage("Player " + player + " has joined the server!");
        board.show(player);
    }

    @Schedule(async = true, period = 20)
    public void updateBoard() {
        board.update();
    }

    @Command("add")
    public void add(Player player) {
        counter++;
        player.sendMessage("Updated counter: " + counter);
    }

}
