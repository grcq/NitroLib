package dev.grcq.nitrolib.spigot.scoreboard;

import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

public class NitroPlayerBoard extends NitroBoard {

    @Setter
    private Function<Player, String> titleFunction;
    @Setter
    private Function<Player, List<String>> linesFunction;

    public NitroPlayerBoard(String title, List<String> lines) {
        this(p -> title, p -> lines);
    }

    public NitroPlayerBoard(Function<Player, String> title, Function<Player, List<String>> lines) {
        this.titleFunction = title;
        this.linesFunction = lines;
    }

    @Override
    protected String getTitle(Player player) {
        return titleFunction.apply(player);
    }

    @Override
    protected List<String> getLines(Player player) {
        return linesFunction.apply(player);
    }
}

