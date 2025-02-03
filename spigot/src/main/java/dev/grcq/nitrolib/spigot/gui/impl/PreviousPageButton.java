package dev.grcq.nitrolib.spigot.gui.impl;

import com.google.common.collect.Lists;
import dev.grcq.nitrolib.spigot.gui.GUIButton;
import dev.grcq.nitrolib.spigot.gui.PaginatedGUI;
import dev.grcq.nitrolib.spigot.utils.ChatUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class PreviousPageButton extends GUIButton {

    private final PaginatedGUI gui;

    private final String name;
    private final List<String> lore;
    private final Material material;
    private final byte data;

    public PreviousPageButton(PaginatedGUI gui) {
        this(gui, "&aPrevious Page", Lists.newArrayList("&7Click to go to the previous page"), Material.ARROW, (byte) 0);
    }

    public PreviousPageButton(PaginatedGUI gui, String name, List<String> lore) {
        this(gui, name, lore, Material.ARROW, (byte) 0);
    }

    public PreviousPageButton(PaginatedGUI gui, String name, List<String> lore, Material material) {
        this(gui, name, lore, material, (byte) 0);
    }

    @Override
    public String getName(Player player) {
        return ChatUtil.format(name);
    }

    @Override
    public List<String> getLore(Player player) {
        return ChatUtil.format(lore);
    }

    @Override
    public Material getMaterial(Player player) {
        return material;
    }

    @Override
    public byte getData(Player player) {
        return data;
    }

    @Override
    public void onClick(Player player, int slot, ClickType type) {
        gui.setPage(player, gui.getPage() - 1);
    }
}
