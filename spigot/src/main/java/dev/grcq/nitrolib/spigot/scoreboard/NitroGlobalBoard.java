package dev.grcq.nitrolib.spigot.scoreboard;

import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public class NitroGlobalBoard extends NitroBoard {

    @Setter
    private Supplier<String> titleSupplier;
    @Setter
    private Supplier<List<String>> linesSupplier;

    public NitroGlobalBoard(String title, List<String> lines) {
        this(() -> title, () -> lines);
    }

    public NitroGlobalBoard(Supplier<String> title, Supplier<List<String>> lines) {
        this.titleSupplier = title;
        this.linesSupplier = lines;
    }

    @Override
    protected String getTitle(Player player) {
        return titleSupplier.get();
    }

    @Override
    protected List<String> getLines(Player player) {
        return linesSupplier.get();
    }
}

