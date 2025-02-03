package dev.grcq.nitrolib.spigot.scoreboard;

import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.function.Function;

public class NitroPlayerBoard extends NitroBoard {

    private final Map<UUID, Scoreboard> scoreboard;

    @Setter
    private Function<Player, String> title;
    @Setter
    private Function<Player, List<String>> lines;

    public NitroPlayerBoard(String title, List<String> lines) {
        this(p -> title, p -> lines);
    }

    public NitroPlayerBoard(Function<Player, String> title, Function<Player, List<String>> lines) {
        this.title = title;
        this.lines = lines;

        this.scoreboard = new HashMap<>();
    }

    public void update() {
        for (UUID uuid : viewers) {
            //System.out.println("Updating scoreboard for " + uuid);
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                viewers.remove(uuid);
                continue;
            }

            //System.out.println(player.getName());

            List<String> lines = this.lines.apply(player);
            if (lines == null) continue;

            //System.out.println(lines);

            update(player.getScoreboard(), lines);
        }
    }

    @Override
    public void show(Player player) {
        if (!viewers.contains(player.getUniqueId())) viewers.add(player.getUniqueId());

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(board);

        scoreboard.put(player.getUniqueId(), board);
        update();
    }

    @Override
    public void hide(Player player) {
        viewers.remove(player.getUniqueId());

        Scoreboard board = scoreboard.get(player.getUniqueId());
        if (board != null) {
            board.clearSlot(DisplaySlot.SIDEBAR);
        }

        scoreboard.remove(player.getUniqueId());
    }

    @Override
    protected String getTitleForBoard(Scoreboard board) {
        if (!scoreboard.containsValue(board)) return title.apply(null);

        for (UUID uuid : scoreboard.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && scoreboard.get(uuid).equals(board)) {
                return title.apply(player);
            }
        }

        return title.apply(null);
    }
}
