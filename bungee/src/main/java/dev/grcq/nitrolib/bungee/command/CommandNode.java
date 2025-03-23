package dev.grcq.nitrolib.bungee.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import dev.grcq.nitrolib.bungee.command.annotations.Command;
import lombok.Data;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.grcq.nitrolib.bungee.command.NitroCommandHandler.INSTANCES;

@Data
public class CommandNode {

    private static final Map<UUID, Pair<String, Long>> cooldowns;

    static {
        cooldowns = new HashMap<>();
    }

    @NotNull
    private final NitroCommandHandler handler;
    @NotNull
    private final Plugin source;

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

    private final boolean playerOnly;
    private final boolean async;
    private final boolean baseExecutor;

    private final int cooldown;
    @Nullable
    private final String bypassCooldownPermission;

    @NotNull
    private final List<CommandNode> children;

    public CommandNode(@NotNull NitroCommandHandler handler, @NotNull Plugin source, @NotNull Command annotation, @NotNull Method method) throws Exception {
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
        this.playerOnly = ProxiedPlayer.class.isAssignableFrom(senderClass);

        this.async = annotation.async();
        this.baseExecutor = annotation.baseExecutor();
        this.cooldown = annotation.cooldown();
        this.bypassCooldownPermission = annotation.bypassCooldownPermission().isEmpty() ? null : annotation.bypassCooldownPermission();

        this.children = Lists.newArrayList();
    }

    public void addChild(@NotNull CommandNode node) {
        this.children.add(node);
    }

    private boolean hasPermission(CommandSender sender) {
        if (permission == null || permission.isEmpty()) return true;
        if (!(sender instanceof ProxiedPlayer)) return true;
        if (sender.hasPermission("*") || sender.hasPermission("*.*")) return true;

        return sender.hasPermission(permission);
    }

    protected void execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
        }
    }

}
