package dev.grcq.nitrolib.spigot.command;

import com.google.common.collect.Lists;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.core.utils.Util;
import dev.grcq.nitrolib.spigot.command.annotations.Command;
import dev.grcq.nitrolib.spigot.command.parameters.TypeParameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class NitroCommandHandler {

    private static final Map<String, CommandNode> COMMANDS;
    private static final Map<Class<?>, TypeParameter<?>> TYPE_PARAMETERS;
    private static CommandMap COMMAND_MAP;

    static {
        COMMANDS = new HashMap<>();
        TYPE_PARAMETERS = new HashMap<>();

        if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) Bukkit.getPluginManager();

            try {
                Field field = manager.getClass().getField("commandMap");
                field.setAccessible(true);
                COMMAND_MAP = (CommandMap) field.get(manager);
            } catch (Exception e) {
                LogUtil.handleException("Unable to retrieve command map!", e);
            }
        }
    }

    public static List<CommandNode> getCommands(JavaPlugin plugin) {
        return COMMANDS.values().stream().filter(node -> node.getSource().getDescription().getFullName().equals(plugin.getDescription().getFullName())).collect(Collectors.toList());
    }

    public static Map<Class<?>, TypeParameter<?>> getTypeParameters() {
        return TYPE_PARAMETERS;
    }

    private final JavaPlugin plugin;

    public NitroCommandHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Getter @Setter private String permissionMessage = "&cYou do not have permission to execute this command!";
    @Getter @Setter private String consoleOnlyMessage = "&cThis command can only be executed by the console!";
    @Getter @Setter private String playerOnlyMessage = "&cThis command can only be executed by a player!";
    @Getter @Setter private String invalidUsageMessage = "&cUsage: /%s";
    @Getter @Setter private String exampleMessage = "&cExample: /%s";
    @Getter @Setter private String cooldownMessage = "&cYou must wait %.2f seconds before executing this command again!";

    public void registerAll(JavaPlugin plugin) {
        Collection<Class<?>> classes = Util.getClassesInPackage(plugin.getClass());
        for (Class<?> clazz : classes) {
            register(clazz);
        }
    }

    public void register(Class<?> clazz) {
        List<Method> methods = Lists.newArrayList(clazz.getDeclaredMethods());
        methods.sort(Comparator.comparingInt(m -> m.getAnnotation(Command.class).value()[0].split(" ").length));

        for (Method method : methods) {
            if (!method.isAnnotationPresent(Command.class)) continue;

            register(method);
        }
    }

    private void register(Method method) {
        for (Method m : method.getDeclaringClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(Command.class)) continue;

            Command command = m.getAnnotation(Command.class);
            CommandNode node;
            try {
                node = new CommandNode(this, plugin, command, m);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            String name = node.getName();
            String[] split = name.split(" ");
            if (split.length > 1) {
                CommandNode parent = COMMANDS.get(split[0]);
                if (parent == null) continue;

                parent.addChild(node);
                continue;
            }

            COMMANDS.put(node.getName(), node);

            BukkitCommand cmd = new BukkitCommand(node);
            if (COMMAND_MAP != null) COMMAND_MAP.register(plugin.getName(), cmd);
        }
    }

}
