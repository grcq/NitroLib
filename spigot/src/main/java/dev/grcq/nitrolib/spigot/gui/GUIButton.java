package dev.grcq.nitrolib.spigot.gui;

import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ToString
@EqualsAndHashCode
public abstract class GUIButton {

    private final String id = UUID.randomUUID().toString();

    abstract public String getName(Player player);
    abstract public List<String> getLore(Player player);
    abstract public Material getMaterial(Player player);
    abstract public void onClick(Player player, int slot, ClickType type);

    public byte getData(Player player) {
        return 0;
    }

    public int getAmount(Player player) {
        return 1;
    }

    public boolean cancelClick(Player player) {
        return true;
    }

    public boolean updateOnClick() {
        return false;
    }

    public boolean glowing(Player player) {
        return false;
    }

    public List<ItemFlag> getFlags(Player player) {
        return Lists.newArrayList();
    }

    public Map<Enchantment, Integer> getEnchants(Player player) {
        return new HashMap<>();
    }

    public ItemMeta buildMeta(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getName(player));
        meta.setLore(getLore(player));
        meta.addItemFlags(getFlags(player).toArray(new ItemFlag[0]));

        if (glowing(player)) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        for (Map.Entry<Enchantment, Integer> entry : getEnchants(player).entrySet()) {
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        return meta;
    }

    public ItemStack build(Player player) {
        ItemStack item = new ItemStack(getMaterial(player), getAmount(player), getData(player));
        ItemMeta meta = buildMeta(player, item);
        item.setItemMeta(meta);
        return item;
    }
}
