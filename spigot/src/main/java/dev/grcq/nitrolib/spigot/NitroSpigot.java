package dev.grcq.nitrolib.spigot;

import dev.grcq.nitrolib.core.NitroLib;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.core.utils.Util;
import dev.grcq.nitrolib.spigot.command.annotations.Schedule;
import dev.grcq.nitrolib.spigot.hologram.Hologram;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class NitroSpigot {

    @Getter
    private static File dataFolder;
    @Getter
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
        dataFolder = new File(source.getDataFolder(), "../NitroLib");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        if (initCore) NitroLib.init(source.getClass(), args);

        LogUtil.info("Initialising NitroLib for Spigot...");

        Hologram.init(source);
        this.initSchedulers();

        LogUtil.info("NitroLib has been successfully initialised for Spigot.");
    }

    private void initSchedulers() {
        for (Class<?> clazz : Util.getClassesInPackage(source.getClass())) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Schedule.class)) continue;

                Schedule schedule = method.getAnnotation(Schedule.class);

                boolean async = schedule.async();
                long delay = schedule.delay();
                long period = schedule.period();

                if (async) {
                    if (delay <= 0L && period <= 0L) {
                        source.getServer().getScheduler().runTaskAsynchronously(source, () -> {
                            try {
                                method.invoke(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }  else if (period <= 0L) {
                        source.getServer().getScheduler().runTaskLaterAsynchronously(source, () -> {
                            try {
                                method.invoke(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, delay);
                    } else {
                        source.getServer().getScheduler().runTaskTimerAsynchronously(source, () -> {
                            try {
                                method.invoke(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, delay, period);
                    }
                } else {
                    if (delay <= 0L && period <= 0L) {
                        source.getServer().getScheduler().runTask(source, () -> {
                            try {
                                method.invoke(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }  else if (period <= 0L) {
                        source.getServer().getScheduler().runTaskLater(source, () -> {
                            try {
                                method.invoke(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, delay);
                    } else {
                        source.getServer().getScheduler().runTaskTimer(source, () -> {
                            try {
                                method.invoke(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, delay, period);
                    }
                }
            }
        }
    }
}
