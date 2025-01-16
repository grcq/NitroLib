package dev.grcq.nitrolib.spigot;

import dev.grcq.nitrolib.core.NitroLib;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class NitroSpigot {

    private static NitroSpigot instance;

    @NotNull
    private final JavaPlugin source;

    public void enable() {
        enable(true);
    }

    public void enable(boolean initCore) {
        enable(initCore, new String[0]);
    }

    public void enable(String[] args) {
        enable(true, args);
    }

    public void enable(boolean initCore, String[] args) {
        if (instance != null) {
            LogUtil.warn("NitroLib is already initialised for this Minecraft server. If you are running another plugin that uses NitroLib, there's no need to initialise it again.");
            LogUtil.warn("We recommend having a core plugin that initialises the library, but it is not required.");
            return;
        }

        instance = this;

        if (initCore) NitroLib.init(source.getClass(), args);

        LogUtil.info("Initialising NitroLib for Spigot...");

        LogUtil.info("NitroLib has been successfully initialised for Spigot.");
    }
}
