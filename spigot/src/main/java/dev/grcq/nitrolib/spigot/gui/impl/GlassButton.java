package dev.grcq.nitrolib.spigot.gui.impl;

import dev.grcq.nitrolib.spigot.gui.GUIButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.List;

public class GlassButton extends GUIButton {

    private final byte data;

    public GlassButton() {
        this((byte) 7);
    }

    public GlassButton(byte data) {
        this.data = data;
    }

    @Override
    public String getName(Player player) {
        return " ";
    }

    @Override
    public List<String> getLore(Player player) {
        return Collections.emptyList();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.STAINED_GLASS_PANE;
    }

    @Override
    public void onClick(Player player, int slot, ClickType type) {
        // Do nothing
    }

    @Override
    public byte getData(Player player) {
        return data;
    }
}
