package dev.grcq.nitrolib.spigot.tests;

import dev.grcq.nitrolib.spigot.gui.GUIButton;
import dev.grcq.nitrolib.spigot.gui.PaginatedGUI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestPagGUI extends PaginatedGUI {

    public TestPagGUI() {
        super(1);
        setAutoUpdate(true);
    }

    @Override
    public String getTitlePerPage(Player player, int page, int maxPage) {
        return "Test GUI";
    }

    @Override
    public int getItemsPerPage(Player player) {
        return 9;
    }

    @Override
    public List<GUIButton> getPageButtons(Player player) {
        List<GUIButton> buttons = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            buttons.add(new TestGUI.TestButton());
        }

        return buttons;
    }
}
