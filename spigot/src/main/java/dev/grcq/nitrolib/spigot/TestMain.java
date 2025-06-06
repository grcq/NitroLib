package dev.grcq.nitrolib.spigot;

import dev.grcq.nitrolib.spigot.command.NitroCommandHandler;
import dev.grcq.nitrolib.spigot.processors.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@Plugin(name = "Spigot", version = "1.0")
public class TestMain extends JavaPlugin {

    private NitroSpigot nitro;

    @Override
    public void onEnable() {
        nitro = new NitroSpigot(this);
        nitro.enable();

        NitroCommandHandler commandHandler = new NitroCommandHandler(this);
        commandHandler.registerAll();
    }

}
