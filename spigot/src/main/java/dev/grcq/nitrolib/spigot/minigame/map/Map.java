package dev.grcq.nitrolib.spigot.minigame.map;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Map {

    @NotNull World getWorld();

    /**
     * Get the lobby spawn location for the map, only for MapType#FULL_SERVER
     * @return the lobby spawn location
     */
    @Nullable Location getLobbySpawn();

}
