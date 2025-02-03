package dev.grcq.nitrolib.spigot.scoreboard;

import dev.grcq.nitrolib.spigot.utils.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public abstract class NitroBoard {

    private final Map<Scoreboard, List<String>> previousLines;
    private final int maxLength;

    private final List<Team> teams;
    protected final List<UUID> viewers;

    public NitroBoard() {
        this.viewers = new ArrayList<>();
        this.teams = new ArrayList<>();

        this.previousLines = new HashMap<>();
        this.maxLength = (ServerVersion.getCurrentVersion().lower(ServerVersion.V1_13) ? 32 : 128);
    }

    public void show(Player player) {
        if (viewers.contains(player.getUniqueId())) return;
        viewers.add(player.getUniqueId());
    }

    public void hide(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        viewers.remove(player.getUniqueId());
    }

    public void update(Scoreboard scoreboard, List<String> lines) {
        if (previousLines.containsKey(scoreboard) && lines.equals(previousLines.get(scoreboard))) return;

        String title = getTitleForBoard(scoreboard);
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) {
            objective = scoreboard.registerNewObjective("board", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(title);
        } else if (!objective.getDisplayName().equals(title)) {
            objective.setDisplayName(title);
        }

        List<String> colorCodes = generateColorCodes(lines.size());
        if (teams.size() != lines.size()) {
            for (Team team : teams) team.unregister();
            teams.clear();

            for (int i = 0; i < lines.size(); i++) {
                Team team = scoreboard.registerNewTeam("line" + i);

                team.addEntry(colorCodes.get(i));
                teams.add(team);

                objective.getScore(colorCodes.get(i)).setScore(lines.size() - i);
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            Team team = teams.get(i);
            String line = lines.get(i);

            if (line.length() > maxLength) line = line.substring(0, maxLength);
            team.setPrefix(line);
        }

        previousLines.put(scoreboard, lines);
    }

    protected abstract String getTitleForBoard(Scoreboard scoreboard);

    private List<String> generateColorCodes(int size) {
        List<String> colorCodes = new ArrayList<>();
        for (ChatColor color : ChatColor.values()) {
            if (color.isFormat()) continue;

            for (ChatColor second : ChatColor.values()) {
                if (second.isFormat()) continue;
                if (color != second && !colorCodes.contains(color + "" + second)) {
                    colorCodes.add(color + "" + second);
                    if (colorCodes.size() == size) break;
                }
            }
        }

        return colorCodes;
    }
}
