package dev.grcq.nitrolib.spigot.hologram;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.spigot.NitroSpigot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Hologram {

    @Getter private static final List<Hologram> holograms = new ArrayList<>();

    private static boolean initialised;
    private static long refreshRate = 20L;

    public static void init(JavaPlugin source) {
        if (initialised) return;
        initialised = true;

        Bukkit.getScheduler().runTaskTimerAsynchronously(source, () -> {
            for (Hologram hologram : holograms) {
                if (hologram.isUpdate() && hologram.isSpawned()) {
                    try {
                        List<String> lines = Lists.reverse(hologram.getLines().call());
                        for (int i = 0; i < lines.size(); i++) {
                            ArmorStand armorStand = hologram.getArmorStands().get(i);
                            armorStand.setCustomName(lines.get(i));
                        }
                    } catch (Exception e) {
                        LogUtil.handleException("Failed to update hologram.", e);
                    }
                }
            }
        }, 20L, refreshRate);
        Bukkit.getScheduler().runTaskLater(source, () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntitiesByClass(ArmorStand.class)) {
                    if (!entity.hasMetadata("hologram")) continue;

                    entity.remove();
                }
            }
        }, 20L * 5);
    }

    @Getter private final String id;
    @Getter private Location location;
    @Getter @Setter private Callable<List<String>> lines;

    @Getter @Setter private boolean update;
    @Getter private boolean spawned;

    @Getter(AccessLevel.PRIVATE)
    private final List<ArmorStand> armorStands;

    public Hologram(String id, Location location, Callable<List<String>> lines, boolean update) {
        this.id = id;
        this.location = location;
        this.lines = lines;
        this.update = update;
        this.armorStands = new ArrayList<>();
    }

    public Hologram(String id, Location location, Callable<List<String>> lines) {
        this(id, location, lines, true);
    }

    public Hologram(String id, Location location, List<String> lines, boolean update) {
        this(id, location, () -> lines, update);
    }

    public Hologram(String id, Location location, List<String> lines) {
        this(id, location, lines, true);
    }

    public Hologram(String id, Location location, String[] lines, boolean update) {
        this(id, location, () -> Lists.newArrayList(lines), update);
    }

    public Hologram(String id, Location location, String... lines) {
        this(id, location, lines, true);
    }

    public Hologram(String id, Location location) {
        this(id, location, new ArrayList<>(), true);
    }

    public void spawn() {
        Preconditions.checkState(!spawned, "Hologram is already spawned.");

        try {
            List<String> lines = Lists.reverse(this.lines.call());
            for (int i = 0; i < lines.size(); i++) {
                ArmorStand armorStand = location.getWorld().spawn(location.clone().add(0, 0.25 + (-i * 0.25), 0), ArmorStand.class);
                armorStand.setMetadata("hologram", new FixedMetadataValue(NitroSpigot.getInstance().getSource(), true));

                armorStand.setGravity(false);
                armorStand.setCanPickupItems(false);
                armorStand.setCustomName(lines.get(i));
                armorStand.setCustomNameVisible(true);
                armorStand.setVisible(false);
                armorStand.setSmall(true);

                armorStands.add(armorStand);
            }

            holograms.add(this);
            spawned = true;
        } catch (Exception e) {
            LogUtil.handleException("Failed to spawn hologram.", e);
        }
    }

    public void despawn() {
        Preconditions.checkState(spawned, "Hologram is not spawned.");

        for (ArmorStand armorStand : armorStands) {
            armorStand.remove();
        }

        armorStands.clear();
        holograms.remove(this);

        spawned = false;
    }

    public void setLocation(Location location) {
        this.location = location;

        if (spawned) {
            for (int i = 0; i < armorStands.size(); i++) {
                ArmorStand armorStand = armorStands.get(i);
                armorStand.teleport(location.clone().add(0, 0.25 + (-i * 0.25), 0));
            }
        }
    }
}
