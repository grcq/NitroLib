package dev.grcq.nitrolib.spigot.schedulers;

import dev.grcq.nitrolib.spigot.NitroSpigot;
import dev.grcq.nitrolib.spigot.tab.TabHandler;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class NitroSchedulers {

    private final NitroSpigot nitro;

    @Schedule(dependencies = "ProtocolLib")
    public void updateTab(Player player) {
        TabHandler.update(player);
    }
}
