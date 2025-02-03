package dev.grcq.nitrolib.spigot.tests;

import com.google.common.collect.Lists;
import dev.grcq.nitrolib.spigot.gui.GUI;
import dev.grcq.nitrolib.spigot.gui.GUIButton;
import dev.grcq.nitrolib.spigot.gui.impl.GlassButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGUI extends GUI {

    public TestGUI() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Test GUI";
    }

    @Override
    public int getSize(Player player) {
        return 9 * 3;
    }

    @Override
    public Map<Integer, GUIButton> getButtons(Player player) {
        Map<Integer, GUIButton> buttons = new HashMap<>();
        GlassButton glassButton = new GlassButton();
        for (int i = 0; i < 9; i++) {
            buttons.put(i, glassButton);
        }

        buttons.put(9, new TestButton());
        return buttons;
    }

    static class TestButton extends GUIButton {
        private int count = 0;

        @Override
        public String getName(Player player) {
            return "Test Button";
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.DIAMOND;
        }

        @Override
        public List<String> getLore(Player player) {
            return Lists.newArrayList("Test Button Lore", "Count: " + count);
        }

        @Override
        public void onClick(Player player, int slot, ClickType type) {
            count++;
            player.sendMessage("Test Button clicked! Slot: " + slot + ", Type: " + type + ", Count: " + count);
        }
    }
}
