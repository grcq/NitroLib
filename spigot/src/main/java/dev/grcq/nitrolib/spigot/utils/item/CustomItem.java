package dev.grcq.nitrolib.spigot.utils.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public interface CustomItem {

    Material getMaterial();
    byte getData();

    String getName();
    List<String> getLore();

    void onClick(Player player, ClickType clickType);

}
