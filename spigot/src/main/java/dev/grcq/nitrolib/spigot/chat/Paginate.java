package dev.grcq.nitrolib.spigot.chat;

import dev.grcq.nitrolib.spigot.utils.ChatUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Paginate<T> {

    @Nullable
    public String getHeaderFooter(int page, int maxPage) {
        return null;
    }

    @Nullable
    public String getHeader(int page, int maxPage) {
        return null;
    }

    public abstract String format(T paramT, int paramInt1, int paramInt2);

    @Nullable
    public String getFooter(int page, int maxPage) {
        return null;
    }

    public final void send(CommandSender sender, int page, List<T> list) {
        send(sender, page, 5, list);
    }

    public final void send(CommandSender sender, int page, int maxItemsPerPage, List<T> list) {
        int maxPage = (int) Math.ceil((double) list.size() / maxItemsPerPage);
        if (page < 1 || page > maxPage) {
            sender.sendMessage(ChatUtil.format("&cThe page '&e" + page + "&c' does not exist."));
            return;
        }

        String header = getHeader(page, maxPage);
        String footer = getFooter(page, maxPage);
        String headerFooter = getHeaderFooter(page, maxPage);
        if (headerFooter != null)
            sender.sendMessage(ChatUtil.format(headerFooter));
        else if (header != null)
            sender.sendMessage(ChatUtil.format(header));

        int fromIndex = (page - 1) * maxItemsPerPage;
        int toIndex = Math.min(page * maxItemsPerPage, list.size());
        for (int i = fromIndex; i < toIndex; i++)
            sender.sendMessage(format(list.get(i), page, maxPage));

        if (headerFooter != null)
            sender.sendMessage(ChatUtil.format(headerFooter));
        else if (footer != null)
            sender.sendMessage(ChatUtil.format(footer));
    }
}
