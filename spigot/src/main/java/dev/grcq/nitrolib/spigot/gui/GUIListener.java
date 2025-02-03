package dev.grcq.nitrolib.spigot.gui;

import dev.grcq.nitrolib.core.utils.KeyValue;
import dev.grcq.nitrolib.spigot.events.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GUIListener {

    @Event
    public void onClick(InventoryClickEvent e) {
        if (e.getClick() == ClickType.DOUBLE_CLICK) return;
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player player = (Player) e.getWhoClicked();
        KeyValue<GUI, Inventory> gui = GUI.OPEN_INVENTORIES.get(player.getUniqueId());
        if (gui == null) return;

        if (!e.getClickedInventory().equals(gui.getValue())) return;
        if (e.getCurrentItem() == null) return;

        GUI guiClass = gui.getKey();
        Map<Integer, GUIButton> items = guiClass.getBuiltButtons();

        int slot = e.getRawSlot();
        GUIButton button = items.get(slot);
        Inventory inventory = e.getClickedInventory();
        if (button == null) return;

        e.setCancelled(button.cancelClick(player));
        button.onClick(player, slot, e.getClick());

        if (button.updateOnClick()) {
            ItemStack item = button.build(player);
            if (item != null) inventory.setItem(slot, item);
            guiClass.onUpdate(player);
        }
    }

    @Event
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;

        Player player = (Player) e.getPlayer();
        KeyValue<GUI, Inventory> gui = GUI.OPEN_INVENTORIES.get(player.getUniqueId());
        if (gui == null) return;

        Inventory inventory = gui.getValue();
        if (!e.getInventory().equals(inventory)) return;

        GUI guiClass = gui.getKey();
        guiClass.onClose(player);

        GUI.OPEN_INVENTORIES.remove(player.getUniqueId());
    }

}
