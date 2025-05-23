package dev.grcq.nitrolib.spigot;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.Lists;
import dev.grcq.nitrolib.core.NitroLib;
import dev.grcq.nitrolib.core.serialization.FileDeserializer;
import dev.grcq.nitrolib.core.serialization.FileSerializer;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.core.utils.Util;
import dev.grcq.nitrolib.spigot.schedulers.Schedule;
import dev.grcq.nitrolib.spigot.events.EventHandler;
import dev.grcq.nitrolib.spigot.hologram.Hologram;
import dev.grcq.nitrolib.spigot.tab.TabHandler;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class NitroSpigot {

    @Getter
    private static File dataFolder;
    @Getter
    private static NitroSpigot instance;
    @Getter
    private NitroConfig config = NitroConfig.DEFAULT;

    @NotNull
    private final JavaPlugin source;
    @Getter
    private Class<?> mainClass = null;
    @Getter
    private boolean protocolLibEnabled = false;
    @Getter
    private ProtocolManager protocolManager = null;

    public void enable() {
        enable(true);
    }

    public void enable(Class<?> mainClass) {
        enable(mainClass, true);
    }

    public void enable(boolean initCore) {
        enable(initCore, new String[0]);
    }

    public void enable(String[] args) {
        enable(true, args);
    }

    public void enable(Class<?> mainClass, String[] args) {
        enable(mainClass, true, args);
    }

    public void enable(Class<?> mainClass, boolean initCore) {
        enable(mainClass, initCore, new String[0]);
    }

    public void enable(boolean initCore, String[] args) {
        enable(source.getClass(), initCore, args);
    }

    public void enable(Class<?> mainClass, boolean initCore, String[] args) {
        if (instance != null) {
            LogUtil.warn("NitroLib is already initialised for this Minecraft server. If you are running another plugin that uses NitroLib, there's no need to initialise it again.");
            LogUtil.warn("We recommend having a core plugin that initialises the library, but it is not required.");
            return;
        }

        this.mainClass = mainClass;

        instance = this;
        this.protocolLibEnabled = source.getServer().getPluginManager().getPlugin("ProtocolLib") != null;
        if (!protocolLibEnabled) {
            LogUtil.warn("ProtocolLib is not installed on this server. Some features that require ProtocolLib will be disabled.");
            LogUtil.warn("We recommend installing ProtocolLib, but it is not required.");
        } else {
            this.protocolManager = ProtocolLibrary.getProtocolManager();
        }

        dataFolder = new File(source.getDataFolder().getParent(), "NitroLib");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        FileSerializer serializer = new FileSerializer();
        FileDeserializer deserializer = new FileDeserializer();

        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            serializer.serialize(config, configFile);
        } else {
            try {
                List<NitroConfig> configs = deserializer.deserialize(configFile, NitroConfig.class);
                if (configs.isEmpty()) LogUtil.error("Failed to load config file. Using default config.");
                else config = configs.get(0);
            } catch (Exception e) {
                LogUtil.handleException("Failed to load config file. Using default config.", e);
            }
        }

        List<String> argsList = Lists.newArrayList(args);
        if (config.isDebugMode()) argsList.add("-d");
        if (initCore) NitroLib.init(mainClass, argsList.toArray(new String[0]));

        LogUtil.info("Initialising NitroLib for Spigot...");

        TabHandler.init(this);
        Hologram.init(source);
        this.initSchedulers();

        EventHandler.registerAll(getClass(), source);

        LogUtil.info("NitroLib has been successfully initialised for Spigot.");
    }

    public void cleanup() {
        if (protocolManager != null) {
            protocolManager.removePacketListeners(source);
        }

        instance = null;
        dataFolder = null;
    }

    private void initSchedulers() {
        Map<Class<?>, Object> instances = new HashMap<>();
        for (Class<?> clazz : Util.getClassesInPackage(mainClass)) {
            mLoop:
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Schedule.class)) continue;

                Schedule schedule = method.getAnnotation(Schedule.class);
                for (String dependency : schedule.dependencies()) {
                    if (!source.getServer().getPluginManager().isPluginEnabled(dependency)) {
                        LogUtil.warn("Failed to register scheduled task for class %s: dependency %s is not found.", clazz.getName(), dependency);
                        continue mLoop;
                    }
                }

                boolean async = schedule.async();
                long delay = schedule.delay();
                long period = schedule.period();

                Runnable runnable = () -> {
                    Object instance = null;
                    if (!Modifier.isStatic(method.getModifiers())) {
                        instance = instances.get(clazz);
                        if (instance == null) {
                            try {
                                Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                                if (constructor.getParameterCount() > 1 || (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] != NitroSpigot.class)) {
                                    LogUtil.warn("Invalid constructor for scheduled task: %s", clazz.getName());
                                    return;
                                }

                                List<Object> params = new ArrayList<>();
                                if (constructor.getParameterCount() == 1) params.add(this);

                                constructor.setAccessible(true);
                                instance = constructor.newInstance(params.toArray());
                                instances.put(clazz, instance);
                            } catch (Exception e) {
                                LogUtil.handleException("Failed to create instance of class %s", e, 5, clazz.getName());
                                return;
                            }
                        }
                    }

                    if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == Player.class) {
                        for (Player player : source.getServer().getOnlinePlayers()) {
                            try {
                                method.invoke(instance, player);
                            } catch (Exception e) {
                                LogUtil.handleException("Failed to invoke scheduled task for player %s", e, 5, player.getName());
                            }
                        }
                        return;
                    }

                    if (method.getParameterCount() != 0) {
                        LogUtil.warn("Invalid method signature for scheduled task: %s", method.getName());
                        return;
                    }

                    try {
                        method.invoke(instance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                method.setAccessible(true);
                if (async) {
                    if (delay <= 0L && period <= 0L) {
                        source.getServer().getScheduler().runTaskAsynchronously(source, runnable);
                    } else if (period <= 0L) {
                        source.getServer().getScheduler().runTaskLaterAsynchronously(source, runnable, delay);
                    } else {
                        source.getServer().getScheduler().runTaskTimerAsynchronously(source, runnable, delay, period);
                    }
                } else {
                    if (delay <= 0L && period <= 0L) {
                        source.getServer().getScheduler().runTask(source, runnable);
                    } else if (period <= 0L) {
                        source.getServer().getScheduler().runTaskLater(source, runnable, delay);
                    } else {
                        source.getServer().getScheduler().runTaskTimer(source, runnable, delay, period);
                    }
                }
            }
        }
    }
}
