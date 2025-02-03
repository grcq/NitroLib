package dev.grcq.nitrolib.spigot.gui;

import com.google.common.base.Preconditions;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.spigot.gui.impl.GlassButton;
import dev.grcq.nitrolib.spigot.gui.impl.NextPageButton;
import dev.grcq.nitrolib.spigot.gui.impl.PreviousPageButton;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PaginatedGUI extends GUI {

    @Getter
    private int page;

    public PaginatedGUI(int page) {
        this.page = Math.max(page, 1);
    }

    abstract public String getTitlePerPage(Player player, int page, int maxPage);
    abstract public int getItemsPerPage(Player player);
    abstract public List<GUIButton> getPageButtons(Player player);

    @Override
    public final String getTitle(Player player) {
        int maxPage = getMaxPage(player);
        return getTitlePerPage(player, page, maxPage) + " (" + page + "/" + maxPage + ")";
    }

    public Map<Integer, GUIButton> getCustomizedButtons(Player player, int page, int maxPage) {
        return new HashMap<>();
    }

    public final int getMaxPage(Player player) {
        return (int) Math.ceil((double) getPageButtons(player).size() / getItemsPerPage(player));
    }

    @Override
    public final int getSize(Player player) {
        int itemsPerPage = getItemsPerPage(player);
        Preconditions.checkArgument(itemsPerPage > 0 && itemsPerPage % 9 == 0, "Items per page must be a multiple of 9");
        return (9 * 2) + (itemsPerPage);
    }

    @Override
    public final Map<Integer, GUIButton> getButtons(Player player) {
        int maxPage = getMaxPage(player);
        if (page > maxPage) {
            page = maxPage;
        }

        Map<Integer, GUIButton> buttons = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            buttons.put(i, new GlassButton());
        }

        int itemsPerPage = getItemsPerPage(player);

        List<GUIButton> pageButtons = getPageButtons(player);
        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, pageButtons.size());
        for (int i = start; i < end; i++) {
            buttons.put(i - start + 9, pageButtons.get(i));
        }

        if (pageButtons.size() - (start) > itemsPerPage) {
            buttons.put(26, new NextPageButton(this));
        }

        if (page > 1) {
            buttons.put(18, new PreviousPageButton(this));
        }

        Map<Integer, GUIButton> customizedButtons = getCustomizedButtons(player, page, maxPage);
        buttons.putAll(customizedButtons);

        return buttons;
    }

    public final void setPage(Player player, int page) {
        this.page = Math.max(page, 1);
        update(player);
    }
}
