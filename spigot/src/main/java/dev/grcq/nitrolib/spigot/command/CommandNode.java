package dev.grcq.nitrolib.spigot.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.spigot.command.annotations.Arg;
import dev.grcq.nitrolib.spigot.command.annotations.Command;
import dev.grcq.nitrolib.spigot.command.annotations.Flag;
import dev.grcq.nitrolib.spigot.command.annotations.FlagValue;
import dev.grcq.nitrolib.spigot.command.parameters.TypeParameter;
import dev.grcq.nitrolib.spigot.command.parser.ArgumentParser;
import dev.grcq.nitrolib.spigot.command.parser.data.ArgData;
import dev.grcq.nitrolib.spigot.command.parser.data.FlagData;
import dev.grcq.nitrolib.spigot.command.parser.data.FlagValueData;
import dev.grcq.nitrolib.spigot.command.parser.data.IData;
import dev.grcq.nitrolib.spigot.utils.ChatUtil;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import static dev.grcq.nitrolib.spigot.command.NitroCommandHandler.INSTANCES;

@Data
public class CommandNode {

    private static final Map<UUID, Pair<String, Long>> COOLDOWNS;

    static {
        COOLDOWNS = Maps.newHashMap();
    }

    @NotNull
    private final NitroCommandHandler handler;
    @NotNull
    private final JavaPlugin source;

    @NotNull
    private final String name;
    @NotNull
    private final List<String> aliases;
    @Nullable
    private final String permission;
    @Nullable
    private final String description;

    @Nullable
    private final String usage;
    @Nullable
    private final String example;

    @NotNull
    private final Method method;
    @NotNull
    private final Object instance;

    private final boolean consoleOnly;
    private final boolean playerOnly;
    private final boolean async;
    private final boolean hidden;
    private final boolean baseExecutor;

    private final int cooldown;
    @Nullable
    private final String bypassCooldownPermission;

    @NotNull
    private final List<CommandNode> children;

    public CommandNode(@NotNull NitroCommandHandler handler, @NotNull JavaPlugin source, @NotNull Command annotation, @NotNull Method method) throws Exception {
        this.handler = handler;
        this.source = source;

        List<String> names = Lists.newArrayList(annotation.value());
        Preconditions.checkArgument(!names.isEmpty(), "Command name cannot be empty!");
        Preconditions.checkArgument(names.stream().noneMatch(String::isEmpty), "Command name cannot be empty!");

        this.name = names.remove(0);
        this.aliases = names;

        this.permission = annotation.permission().isEmpty() ? null : annotation.permission();
        this.description = annotation.description().isEmpty() ? null : annotation.description();
        this.usage = annotation.usage().isEmpty() ? null : annotation.usage();
        this.example = annotation.example().isEmpty() ? null : annotation.example();

        Preconditions.checkArgument(method.getParameterCount() > 0, "Command requires at least one parameter that is an instance of CommandSender!");
        Preconditions.checkArgument(CommandSender.class.isAssignableFrom(method.getParameterTypes()[0]), "First parameter must be an instance of CommandSender!");
        this.method = method;
        this.instance = INSTANCES.get(method.getDeclaringClass());
        if (instance == null) {
            throw new IllegalArgumentException("Instance for class " + method.getDeclaringClass().getName() + " not found!");
        }

        Class<?> senderClass = method.getParameterTypes()[0];
        this.consoleOnly = ConsoleCommandSender.class.isAssignableFrom(senderClass);
        this.playerOnly = Player.class.isAssignableFrom(senderClass);
        if (this.consoleOnly && this.playerOnly) throw new IllegalArgumentException("how did you even end up in this situation?");

        this.hidden = annotation.hidden();
        this.async = annotation.async();
        this.baseExecutor = annotation.baseExecutor();
        this.cooldown = annotation.cooldown();
        this.bypassCooldownPermission = annotation.bypassCooldownPermission().isEmpty() ? null : annotation.bypassCooldownPermission();

        this.children = Lists.newArrayList();
    }

    private boolean hasPermission(CommandSender sender) {
        if (permission == null || permission.isEmpty()) return true;
        if (sender.isOp()) return true;
        if (sender.hasPermission("*") || sender.hasPermission("*.*")) return true;

        return sender.hasPermission(permission);
    }

    protected void execute(CommandSender sender, String label, String[] args) throws Exception {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(source, () -> {
                try {
                    execute(sender, label, args);
                } catch (Exception e) {
                    exception(sender, e);
                }
            });
            return;
        }
        if (!hasPermission(sender)) {
            sender.sendMessage(ChatUtil.format(handler.getPermissionMessage()));
            return;
        }

        if (cooldown > 0 && sender instanceof Player && (bypassCooldownPermission == null || !sender.hasPermission(bypassCooldownPermission))) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            Pair<String, Long> pair = COOLDOWNS.getOrDefault(uuid, Pair.of(name, 0L));

            long last = pair.getRight();
            long diff = System.currentTimeMillis() - last;
            if (diff < cooldown * 1000L) {
                long remaining = (cooldown - diff) / 1000;
                sender.sendMessage(ChatUtil.format(handler.getCooldownMessage(), remaining));
                return;
            }
        }

        if (this.playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.format(handler.getPlayerOnlyMessage()));
            return;
        }

        if (this.consoleOnly && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatUtil.format(handler.getConsoleOnlyMessage()));
            return;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(ChatUtil.format(handler.getPermissionMessage()));
            return;
        }

        LogUtil.debug("%s", Arrays.asList(args));
        List<String> argsList = Lists.newArrayList(args);
        for (int i = args.length - 1; i >= 0; i--) {
            for (CommandNode node : children) {
                List<String> names = Lists.newArrayList(node.name);
                names.addAll(node.aliases);

                String arg = String.join(" ", argsList);
                LogUtil.debug("%s %s", arg, names);
                if (names.contains(label + " " + arg)) {
                    LogUtil.debug("Executing child command %s", node.name);
                    String joined = String.join(" ", args).replaceFirst(arg, "").trim();
                    LogUtil.debug("Joined: %s", joined);
                    String[] newArgs = joined.isEmpty() ? new String[0] : joined.split(" ");
                    node.execute(sender, label + " " + arg, newArgs);
                    return;
                }
            }

            argsList.remove(i);
        }

        List<Parameter> parameters = Lists.newArrayList(method.getParameters());
        parameters.remove(0);

        ArgumentParser parser = new ArgumentParser(parameters.toArray(new Parameter[0]));
        List<IData> data = parser.parse(sender, args);
        if (data == null) {
            return;
        }

        LogUtil.debug("Executing command %s with %d arguments %d", name, data.size(), parameters.size());
        LogUtil.debug("Arguments: %s", data);
        LogUtil.debug("Parameters: %s", parameters);
        LogUtil.debug("%b %b", data.isEmpty(), !parameters.isEmpty());
        if ((data.isEmpty() && !parameters.isEmpty()) || data.size() != parameters.size()) {
            sendUsage(sender, label);
            return;
        }

        List<Object> arguments = Lists.newArrayList();
        arguments.add(sender);
        for (IData d : data) {
            if (d == null) {
                sendUsage(sender, label);
                return;
            }

            if (d instanceof ArgData) {
                ArgData argData = (ArgData) d;
                if (argData.isRequired() && argData.getValue() == null) {
                    sendUsage(sender, label);
                    return;
                }

                arguments.add(argData.getValue());
            }

            if (d instanceof FlagData) {
                FlagData flagData = (FlagData) d;
                arguments.add(flagData.isValue());
            }

            if (d instanceof FlagValueData) {
                FlagValueData flagValueData = (FlagValueData) d;
                arguments.add(flagValueData.getValue());
            }
        }

        LogUtil.debug("%s %s", arguments, parameters.size());
        if (arguments.size() - 1 != parameters.size()) {
            sendUsage(sender, label);
            return;
        }

        method.invoke(instance, arguments.toArray());
    }

    private void sendUsage(CommandSender sender, String label) {
        StringBuilder builder = new StringBuilder(label).append(" ");
        if (children.isEmpty()) {
            if (usage != null) {
                builder.append(ChatUtil.format(handler.getInvalidUsageMessage(), usage));
            } else {
                LogUtil.debug("usage");
                for (Parameter parameter : method.getParameters()) {
                    LogUtil.debug("parameter %s", parameter.getName());
                    if (parameter.isAnnotationPresent(Arg.class)) {
                        Arg arg = parameter.getAnnotation(Arg.class);
                        LogUtil.debug("arg %s", arg.value());
                        if (!arg.def().isEmpty() || !arg.required()) {
                            builder.append("[").append(arg.value()).append("] ");
                        } else {
                            builder.append("<").append(arg.value()).append("> ");
                        }
                    } else if (parameter.isAnnotationPresent(Flag.class)) {
                        Flag flag = parameter.getAnnotation(Flag.class);
                        builder.append("[-").append(flag.value()).append("] ");
                    } else if (parameter.isAnnotationPresent(FlagValue.class)) {
                        FlagValue flagValue = parameter.getAnnotation(FlagValue.class);
                        builder.append("[-").append(flagValue.name()).append(" ");
                        if (!flagValue.def().isEmpty() || !flagValue.required()) {
                            builder.append("[").append(flagValue.arg()).append("]] ");
                        } else {
                            builder.append("<").append(flagValue.arg()).append(">] ");
                        }
                    }
                }
            }

            sender.sendMessage(ChatUtil.format(handler.getInvalidUsageMessage(), builder.toString()));
            return;
        }

        CommandNode base = NitroCommandHandler.getCommand(label);
        if (base != null) {
            try {
                base.execute(sender, label, new String[0]);
            } catch (Exception e) {
                exception(sender, e);
            }
        }
    }

    protected void exception(CommandSender sender, Exception e) {
        sender.sendMessage(ChatUtil.format("&cAn error occurred while executing this command."));
        if (sender.isOp() && e.getMessage() != null) {
            sender.sendMessage(ChatUtil.format("&c" + e.getClass().getName() + ": " + e.getMessage()));
        }

        LogUtil.handleException("An error occurred while executing command '" + name + "'", e);
    }

    public List<String> tabComplete(CommandSender sender, String label, String[] args, int index) {
        List<String> completions = Lists.newArrayList();
        List<String> arguments = Lists.newArrayList(args);
        List<String> flags = Lists.newArrayList();
        for (Parameter parameter : Arrays.stream(method.getParameters()).filter(p -> p.isAnnotationPresent(Flag.class) || p.isAnnotationPresent(FlagValue.class)).toArray(Parameter[]::new)) {
            if (parameter.isAnnotationPresent(Flag.class)) {
                Flag flag = parameter.getAnnotation(Flag.class);
                flags.add(flag.value());

                arguments.remove("-" + flag.value());
            }

            if (parameter.isAnnotationPresent(FlagValue.class)) {
                FlagValue flagValue = parameter.getAnnotation(FlagValue.class);
                if (index - 1 < 0) continue;

                boolean previousFlag = Arrays.asList(args).get(index - 1).equals("-" + flagValue.name());
                if (previousFlag) {
                    flags.add(flagValue.name());

                    Class<?> type = parameter.getType();
                    TypeParameter<?> param = NitroCommandHandler.getTypeParameters().get(type);
                    if (param == null) {
                        LogUtil.error("No type parameter found for " + type.getName());
                        return new ArrayList<>();
                    }

                    completions.addAll(param.tabComplete(sender, flags.toArray(new String[0]), args[index]));
                }
            }
        }

        args = arguments.toArray(new String[0]);

        String arg = args.length > index ? arguments.get(index) : null;
        if (arg == null) return Lists.newArrayList();

        for (CommandNode child : children) {
            List<String> names = Lists.newArrayList(child.name);
            names.addAll(child.aliases);

            for (String name : names) {
                String[] split = name.split(" ");
                if (split.length <= index + 1) continue;

                completions.add(split[index + 1]);
            }
        }

        main: for (CommandNode child : children) {
            List<String> names = Lists.newArrayList(child.name);
            names.addAll(child.aliases);
            LogUtil.debug("Names: %s", names);

            for (int i = args.length - 2; i >= 0; i--) {
                List<String> argsList = Lists.newArrayList(args);
                argsList.remove(argsList.size() - 1);

                String argName = String.join(" ", argsList).trim();
                LogUtil.debug("ArgName: %s", argName);

                for (String name : names) {
                    if (name.equalsIgnoreCase(label + " " + argName)) {
                        String fullArg = String.join(" ", args).replaceFirst(argName, "").trim();
                        String[] newArgs = fullArg.split(" ");
                        completions.addAll(child.tabComplete(sender, name, newArgs, newArgs.length - 1));
                        break main;
                    }

                    argsList.remove(i);
                }
            }
        }

        int i = 0;
        for (Parameter parameter : method.getParameters()) {
            if (!parameter.isAnnotationPresent(Arg.class)) continue;
            LogUtil.debug("Parameter %s", parameter.getName());
            i++;
            LogUtil.debug("Index %d %d", i, index);

            if (i != index + 1) continue;

            Class<?> type = parameter.getType();
            TypeParameter<?> param = NitroCommandHandler.getTypeParameters().get(type);
            if (param == null) {
                LogUtil.error("No type parameter found for " + type.getName());
                return new ArrayList<>();
            }

            completions.addAll(param.tabComplete(sender, flags.toArray(new String[0]), arg));
            break;
        }

        return completions.stream().filter(c -> c.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }

    public void addChild(CommandNode node) {
        children.add(node);
    }
}
