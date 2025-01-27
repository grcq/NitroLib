package dev.grcq.nitrolib.spigot.utils;

import com.google.common.collect.Lists;
import dev.grcq.nitrolib.spigot.NitroSpigot;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class RayTrace {


    private final Location location;
    private final long interval;
    private double distance;
    private double speed;
    private double maxDistance = 100;

    private Location current;
    private Vector direction;

    private Map<RayTraceEventType, List<RayTraceEvent>> events;

    public RayTrace(Location location) {
        this(location, 1);
    }

    public RayTrace(Location location, double speed) {
        this(location, speed, 0);
    }

    public RayTrace(Location location, long interval) {
        this(location, 1, interval);
    }

    /**
     * @param location The location to start the ray trace from
     * @param interval How often the ray trace should be checked, default is 0 (instant)
     */
    public RayTrace(Location location, double speed, long interval) {
        this.location = location;
        this.interval = interval;
        this.speed = speed;
        this.distance = 0;

        this.current = location.clone();
        this.direction = location.getDirection();
    }

    public void trace() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {

                current.add(direction.multiply(speed));
                distance += speed;

                List<RayTraceEvent> events = RayTrace.this.events.get(RayTraceEventType.UPDATE);
                for (RayTraceEvent event : events) {
                    if (event.event(current, distance)) {
                        cancel();
                        return;
                    }
                }

                if (current.getBlock().getType().isSolid()) {
                    List<RayTraceEvent> blockEvents = RayTrace.this.events.get(RayTraceEventType.BLOCK_HIT);
                    for (RayTraceEvent event : blockEvents) {
                        if (event.event(current, distance)) {
                            cancel();
                            return;
                        }
                    }
                }

                List<RayTraceEvent> entityEvents = RayTrace.this.events.get(RayTraceEventType.ENTITY_HIT);
                for (Entity entity : current.getWorld().getNearbyEntities(current, 0.5, 0.5, 0.5)) {
                    for (RayTraceEvent event : entityEvents) {
                        if (event.event(entity.getLocation(), distance)) {
                            cancel();
                            return;
                        }
                    }
                }

                if (distance >= maxDistance) cancel();
            }
        };

        runnable.runTaskTimer(NitroSpigot.getInstance().getSource(), 0, interval);
    }

    public RayTrace setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public RayTrace setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
        return this;
    }

    public RayTrace on(RayTraceEventType type, RayTraceEvent event) {
        this.events.putIfAbsent(type, Lists.newArrayList());
        this.events.get(type).add(event);
        return this;
    }

    public enum RayTraceEventType {
        ENTITY_HIT,
        BLOCK_HIT,
        UPDATE
    }

    @FunctionalInterface
    public interface RayTraceEvent {
        boolean event(Location location, double distance);
    }
}
