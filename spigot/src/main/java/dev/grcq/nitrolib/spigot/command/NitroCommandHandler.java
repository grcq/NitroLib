package dev.grcq.nitrolib.spigot.command;

import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.core.utils.Util;
import dev.grcq.nitrolib.spigot.command.annotations.Command;
import dev.grcq.nitrolib.spigot.command.parameters.TypeParameter;
import dev.grcq.nitrolib.spigot.command.parameters.impl.IntegerType;
import dev.grcq.nitrolib.spigot.command.parameters.impl.PlayerType;
import dev.grcq.nitrolib.spigot.command.parameters.impl.StringType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class NitroCommandHandler {

    private static final Map<String, CommandNode> COMMANDS;
    private static final Map<Class<?>, TypeParameter<?>> TYPE_PARAMETERS;
    protected static final Map<Class<?>, Object> INSTANCES;
    private static CommandMap COMMAND_MAP;

    static {
        COMMANDS = new HashMap<>();
        TYPE_PARAMETERS = new HashMap<>();
        INSTANCES = new HashMap<>();

        register(String.class, new StringType());
        register(Player.class, new PlayerType());
        register(Integer.class, new IntegerType());
        register(int.class, new IntegerType());
    }

    protected static CommandNode getCommand(String name) {
        return COMMANDS.get(name);
    }

    public static List<CommandNode> getCommands(JavaPlugin plugin) {
        return COMMANDS.values().stream().filter(node -> node.getSource().getDescription().getFullName().equals(plugin.getDescription().getFullName())).collect(Collectors.toList());
    }

    public static Map<Class<?>, TypeParameter<?>> getTypeParameters() {
        return TYPE_PARAMETERS;
    }

    public static void register(Class<?> clazz, TypeParameter<?> parameter) {
        TYPE_PARAMETERS.put(clazz, parameter);
    }

    private final JavaPlugin plugin;

    public NitroCommandHandler(JavaPlugin plugin) {
        this.plugin = plugin;

        updateCommandMap();
    }

    private void updateCommandMap() {
        if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) Bukkit.getPluginManager();

            try {
                Field field = manager.getClass().getDeclaredField("commandMap");
                field.setAccessible(true);
                COMMAND_MAP = (CommandMap) field.get(manager);
            } catch (Exception e) {
                LogUtil.handleException("Unable to retrieve command map!", e);
            }
        }
    }

    @Getter @Setter private String permissionMessage = "&cYou do not have permission to execute this command!";
    @Getter @Setter private String consoleOnlyMessage = "&cThis command can only be executed by the console!";
    @Getter @Setter private String playerOnlyMessage = "&cThis command can only be executed by a player!";
    @Getter @Setter private String invalidUsageMessage = "&cUsage: /%s";
    @Getter @Setter private String exampleMessage = "&cExample: /%s";
    @Getter @Setter private String cooldownMessage = "&cYou must wait %.2f seconds before executing this command again!";

    public void registerAll() {
        registerAll(plugin.getClass());
    }

    public void registerAll(Class<?> mainClass) {
        Collection<Class<?>> classes = Util.getClassesInPackage(mainClass);
        for (Class<?> clazz : classes) {
            register(clazz);
        }
    }

    public void register(Class<?> clazz) {
        List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Command.class))
                .sorted(Comparator.comparingInt(m -> m.getAnnotation(Command.class).value()[0].split(" ").length))
                .collect(Collectors.toList());

        for (Method method : methods) {
            if (!method.isAnnotationPresent(Command.class)) continue;

            register(method);
        }
    }

    private void register(Method method) {
        for (Method m : method.getDeclaringClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(Command.class)) continue;

            try {
                Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                INSTANCES.put(method.getDeclaringClass(), instance);
            } catch (Exception e) {
                LogUtil.handleException("Failed to create instance of class " + method.getDeclaringClass().getSimpleName(), e);
                return;
            }
            Command command = m.getAnnotation(Command.class);
            CommandNode node;
            try {
                node = new CommandNode(this, plugin, command, m);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            COMMANDS.put(node.getName(), node);
            for (String alias : node.getAliases()) {
                if (COMMANDS.containsKey(alias)) LogUtil.warn("Command alias '%s' is already registered. This could lead to some issues, please change the name to prevent issues.", alias);
                COMMANDS.put(alias, node);
            }

            String name = node.getName();
            String[] split = name.split(" ");
            if (split.length > 1) {
                CommandNode parent = COMMANDS.get(split[0]);
                if (parent == null) continue;

                parent.addChild(node);
                continue;
            }

            BukkitCommand cmd = new BukkitCommand(node);
            if (COMMAND_MAP != null) COMMAND_MAP.register(plugin.getName(), cmd);
        }
    }

}
