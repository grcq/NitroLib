package dev.grcq.nitrolib.bungee.command;

import dev.grcq.nitrolib.bungee.utils.ChatUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeeCommand extends Command implements TabExecutor {

    public BungeeCommand(CommandNode node) {
        super(node.getName(), node.getPermission(), node.getAliases().toArray(new String[0]));

        if (node.getPermission() != null) {
            NitroCommandHandler handler = node.getHandler();
            this.setPermissionMessage(ChatUtil.format(handler.getPermissionMessage()));
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return null;
    }
}
