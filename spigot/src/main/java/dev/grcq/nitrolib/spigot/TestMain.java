package dev.grcq.nitrolib.spigot;

import dev.grcq.nitrolib.spigot.command.NitroCommandHandler;
import dev.grcq.nitrolib.spigot.events.EventHandler;
import dev.grcq.nitrolib.spigot.processors.plugin.Plugin;
import dev.grcq.nitrolib.spigot.tests.TestListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Plugin(name = "Nitrotest", version = "1.0")
public class TestMain extends JavaPlugin {

    private NitroSpigot nitro;

    @Override
    public void onEnable() {
        nitro = new NitroSpigot(this);
        nitro.enable();

        NitroCommandHandler commandHandler = new NitroCommandHandler(this);
        commandHandler.register(TestListener.class);

        EventHandler.register(TestListener.class, this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, TestListener::updateBoard, 0L, 20L);
    }

}
