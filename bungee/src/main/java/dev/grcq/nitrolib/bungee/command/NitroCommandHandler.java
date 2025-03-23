package dev.grcq.nitrolib.bungee.command;

import dev.grcq.nitrolib.bungee.command.annotations.Command;
import dev.grcq.nitrolib.bungee.parameters.TypeParameter;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.core.utils.Util;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class NitroCommandHandler {

    private static final Map<String, CommandNode> COMMANDS;
    private static final Map<Class<?>, TypeParameter<?>> TYPE_PARAMETERS;
    protected static final Map<Class<?>, Object> INSTANCES;

    static {
        COMMANDS = new HashMap<>();
        TYPE_PARAMETERS = new HashMap<>();
        INSTANCES = new HashMap<>();
    }

    protected static CommandNode getCommand(String name) {
        return COMMANDS.get(name);
    }

    public static List<CommandNode> getCommands(Plugin plugin) {
        return COMMANDS.values().stream().filter(node -> node.getSource().getDescription().getName().equals(plugin.getDescription().getName())).collect(Collectors.toList());
    }

    public static Map<Class<?>, TypeParameter<?>> getTypeParameters() {
        return TYPE_PARAMETERS;
    }

    public static void register(Class<?> clazz, TypeParameter<?> parameter) {
        TYPE_PARAMETERS.put(clazz, parameter);
    }

    private final Plugin plugin;

    public NitroCommandHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Getter
    @Setter
    private String permissionMessage = "&cYou do not have permission to execute this command!";
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

            BungeeCommand cmd = new BungeeCommand(node);
            plugin.getProxy().getPluginManager().registerCommand(plugin, cmd);
        }
    }

}
