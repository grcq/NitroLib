package dev.grcq.nitrolib.spigot.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.spigot.command.annotations.Arg;
import dev.grcq.nitrolib.spigot.command.annotations.Command;
import dev.grcq.nitrolib.spigot.command.annotations.Flag;
import dev.grcq.nitrolib.spigot.command.annotations.FlagValue;
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
    @Nullable
    private final CommandNode parent;

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
        this(handler, source, null, annotation, method);
    }

    public CommandNode(@NotNull NitroCommandHandler handler, @NotNull JavaPlugin source, @Nullable CommandNode parent, @NotNull Command annotation, @NotNull Method method) throws Exception {
        this.handler = handler;
        this.source = source;
        this.parent = parent;

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
        Preconditions.checkArgument(method.getParameterTypes()[0].isAssignableFrom(CommandSender.class), "First parameter must be an instance of CommandSender!");
        this.method = method;
        this.instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();

        Class<?> senderClass = method.getParameterTypes()[0];
        this.consoleOnly = senderClass.isAssignableFrom(ConsoleCommandSender.class);
        this.playerOnly = senderClass.isAssignableFrom(Player.class);
        if (this.consoleOnly && this.playerOnly) throw new IllegalArgumentException("how did you even end up in this situation?");

        this.hidden = annotation.hidden();
        this.async = annotation.async();
        this.baseExecutor = annotation.baseExecutor();
        this.cooldown = annotation.cooldown();
        this.bypassCooldownPermission = annotation.bypassCooldownPermission().isEmpty() ? null : annotation.bypassCooldownPermission();

        this.children = Lists.newArrayList();
    }

    private boolean hasPermission(CommandSender sender) {
        if (permission == null) return true;
        return sender.hasPermission(permission);
    }

    protected void execute(CommandSender sender, String label, String[] args) throws Exception {
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

        CommandNode childNode = null;
        for (CommandNode node : children) {
            List<String> names = Lists.newArrayList(node.name);
            names.addAll(node.aliases);

            List<String> argsList = Lists.newArrayList(args);
            for (int i = args.length - 1; i >= 0; i--) {
                String arg = String.join(" ", argsList);
                if (names.contains(arg)) {
                    childNode = node;
                    break;
                }

                argsList.remove(i);
            }
        }

        if (childNode != null) {
            childNode.execute(sender, label, args);
            return;
        }

        Parameter[] parameters = method.getParameters();
        ArgumentParser parser = new ArgumentParser(parameters);
        List<IData> data = parser.parse(sender, args);

        if (data.isEmpty() && parameters.length > 1) {
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

        if (arguments.size() != parameters.length) {
            sendUsage(sender, label);
            return;
        }

        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(source, () -> {
                try {
                    method.invoke(instance, arguments.toArray());
                } catch (Exception e) {
                    exception(sender, e);
                }
            });
        } else {
            method.invoke(instance, arguments.toArray());
        }
    }

    public CommandNode getBaseExecutorNode() {
        CommandNode node = this;
        while (node != null && !node.isBaseExecutor()) {
            node = node.getParent();
        }
        return node;
    }

    private void sendUsage(CommandSender sender, String label) {
        StringBuilder builder = new StringBuilder(label).append(" ");
        if (children.isEmpty()) {
            if (usage != null) {
                builder.append(ChatUtil.format(handler.getInvalidUsageMessage(), usage));
            } else {
                for (Class<?> parameter : method.getParameterTypes()) {
                    if (parameter.isAnnotationPresent(Arg.class)) {
                        Arg arg = parameter.getAnnotation(Arg.class);
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

        CommandNode base = getBaseExecutorNode();
        if (base != null) {
            try {
                base.execute(sender, label, new String[0]);
            } catch (Exception e) {
                exception(sender, e);
            }

            return;
        }
    }

    protected void exception(CommandSender sender, Exception e) {
        sender.sendMessage(ChatUtil.format("&cAn error occurred while executing this command."));
        if (sender.isOp() && e.getMessage() != null) {
            sender.sendMessage(ChatUtil.format("&c" + e.getMessage()));
        }

        LogUtil.handleException("An error occurred while executing command " + name, e);
    }

    public List<String> tabComplete(CommandSender sender, String[] args, int index) {
        List<String> arguments = Lists.newArrayList(args);
        List<String> flags = Lists.newArrayList();
        for (Parameter parameter : Arrays.stream(method.getParameters()).filter(p -> p.isAnnotationPresent(Flag.class) || p.isAnnotationPresent(FlagValue.class)).toArray(Parameter[]::new)) {
            Flag flag = parameter.getAnnotation(Flag.class);
            flags.add(flag.value());

            arguments.remove("-" + flag.value());
        }

        String arg = arguments.size() > index ? arguments.get(index) : null;
        if (arg == null) return Lists.newArrayList();

        List<String> completions = Lists.newArrayList();
        for (CommandNode node : children) {
            List<String> names = Lists.newArrayList(node.name);
            names.addAll(node.aliases);

            for (String name : names) {
                String[] split = name.split(" ");
                if (split.length <= index) continue;

                String n = split[index];
                completions.add(n);

                if (arguments.size() > index && arguments.get(index).equalsIgnoreCase(name)) {
                    String[] shiftedArgs = null; // todo
                    names.addAll(node.tabComplete(sender, shiftedArgs, 0));
                }
            }
        }

        return completions;
    }

    public void addChild(CommandNode node) {
        children.add(node);
    }
}
