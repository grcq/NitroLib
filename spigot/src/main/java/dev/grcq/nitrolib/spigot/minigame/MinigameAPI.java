package dev.grcq.nitrolib.spigot.minigame;

import dev.grcq.nitrolib.spigot.minigame.map.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class MinigameAPI<T> {

    @Setter @Getter
    private GameState state = GameState.WAITING;
    @Getter
    private Map activeMap;

    abstract public GameType getGameType();
    abstract public MapType getMapType();

    public void onStateChange(GameState state) {

    }

    abstract public List<Map> getMaps();

    abstract public void addPlayer(T player);
    abstract public void removePlayer(T player);
    abstract public List<T> getPlayers();

    abstract public int getMinPlayers();
    abstract public int getMaxPlayers();

    abstract public int getPlayersPerTeam();

    public void start() {

    }

    public void end() {

    }
}
