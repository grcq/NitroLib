package dev.grcq.nitrolib.spigot.gui;

import dev.grcq.nitrolib.core.utils.KeyValue;
import dev.grcq.nitrolib.spigot.NitroSpigot;
import dev.grcq.nitrolib.spigot.utils.ChatUtil;
import dev.grcq.nitrolib.spigot.utils.NMSUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class GUI {

    public static final Map<UUID, KeyValue<GUI, Inventory>> OPEN_INVENTORIES = new HashMap<>();

    @Getter
    private boolean autoUpdate = false;
    @Getter
    private long updateInterval = 15L;
    @Getter(value = AccessLevel.PROTECTED)
    private final Map<Integer, GUIButton> builtButtons = new HashMap<>();

    private BukkitRunnable runnable;
    private BukkitTask task;

    abstract public String getTitle(Player player);

    abstract public int getSize(Player player);

    abstract public Map<Integer, GUIButton> getButtons(Player player);

    public final void open(Player player) {
        Inventory inventory = player.getServer().createInventory(player, getSize(player), ChatUtil.format(getTitle(player)));
        OPEN_INVENTORIES.put(player.getUniqueId(), KeyValue.of(this, inventory));

        Map<Integer, GUIButton> buttons = getButtons(player);
        builtButtons.putAll(buttons);

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !OPEN_INVENTORIES.containsKey(player.getUniqueId())) {
                    OPEN_INVENTORIES.remove(player.getUniqueId()); // to make sure it's removed
                    cancel();
                    return;
                }

                refresh(player);
            }
        };
        runnable.runTask(NitroSpigot.getInstance().getSource());

        player.openInventory(inventory);
        onOpen(player);

        if (autoUpdate) {
            if (task != null) task.cancel();
            task = runnable.runTaskTimerAsynchronously(NitroSpigot.getInstance().getSource(), updateInterval, updateInterval);
        }
    }

    public final void update(Player player) {
        if (!OPEN_INVENTORIES.containsKey(player.getUniqueId())) return;

        Inventory inventory = OPEN_INVENTORIES.get(player.getUniqueId()).getValue();
        String name = ChatUtil.format(getTitle(player));
        if (!inventory.getTitle().equals(name)) {
            NMSUtil.setInventoryTitle(player, inventory, name);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            GUIButton button = builtButtons.get(i);
            if (button == null) {
                inventory.setItem(i, null);
                continue;
            }

            inventory.setItem(i, button.build(player));
        }

        onUpdate(player);
    }

    public final void refresh(Player player) {
        if (!OPEN_INVENTORIES.containsKey(player.getUniqueId())) return;

        Map<Integer, GUIButton> buttons = getButtons(player);
        for (int i = 0; i < getSize(player); i++) {
            GUIButton button = buttons.get(i);
            if (button == null) {
                builtButtons.remove(i);
                continue;
            }

            builtButtons.putIfAbsent(i, button);
        }

        update(player);
    }

    public void onOpen(Player player) {

    }

    public void onUpdate(Player player) {

    }

    public void onClose(Player player) {

    }

    public final void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;

        if (!autoUpdate && task != null) {
            task.cancel();
            task = null;
        }

        if (autoUpdate && task == null && runnable != null) {
            task = runnable.runTaskTimerAsynchronously(NitroSpigot.getInstance().getSource(), updateInterval, updateInterval);
        }
    }

    public final void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;

        if (task != null && runnable != null) {
            task.cancel();
            task = runnable.runTaskTimerAsynchronously(NitroSpigot.getInstance().getSource(), updateInterval, updateInterval);
        }
    }
}
