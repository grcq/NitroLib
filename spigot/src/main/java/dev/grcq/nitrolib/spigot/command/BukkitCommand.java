package dev.grcq.nitrolib.spigot.command;

import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.spigot.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.List;

public class BukkitCommand extends Command {

    private final CommandNode node;

    public BukkitCommand(CommandNode node) {
        super(node.getName(), node.getDescription() == null ? "" : node.getDescription(), node.getUsage() == null ? "" : node.getUsage(), node.getAliases());
        this.node = node;

        if (node.getPermission() != null) {
            this.setPermission(node.getPermission());
            if (node.isHidden()) {
                try {
                    Class<?> spigotConfig = Class.forName("org.spigotmc.SpigotConfig");
                    Field unknownCommand = spigotConfig.getDeclaredField("unknownCommandMessage");
                    unknownCommand.setAccessible(true);

                    this.setPermissionMessage(ChatUtil.format(((String) unknownCommand.get(spigotConfig))));
                } catch (Exception e) {
                    LogUtil.handleException("Failed to hide command " + node.getName(), e);
                }
            } else this.setPermissionMessage(ChatUtil.format(node.getHandler().getPermissionMessage()));
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try {
            node.execute(sender, label, args);
            return true;
        } catch (Exception e) {
            node.exception(sender, e);
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return node.tabComplete(sender, alias, args, args.length - 1);
    }
}
