package dev.grcq.nitrolib.spigot.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.grcq.nitrolib.core.annotations.Inject;
import dev.grcq.nitrolib.core.utils.LogUtil;
import dev.grcq.nitrolib.spigot.NitroSpigot;
import dev.grcq.nitrolib.spigot.hologram.Hologram;
import dev.grcq.nitrolib.spigot.utils.NMSUtil;
import dev.grcq.nitrolib.spigot.utils.PlayerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class NPC {

    @Getter
    private static final List<NPC> npcs;
    private static Team TEAM;

    static {
        npcs = new ArrayList<>();
        TEAM = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("npcs");
        if (TEAM == null) TEAM = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("npcs");

        TEAM.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        TEAM.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        TEAM.setPrefix(ChatColor.GRAY + "[NPC] ");
        TEAM.setColor(ChatColor.GRAY);
    }

    @Inject
    private NitroSpigot nitro;

    @Getter
    private final @NotNull String id;
    @Getter
    private @NotNull Location location;

    private @Nullable String texture;
    private @Nullable String signature;

    @Getter
    private @Nullable NPCAction action;

    private GameProfile profile;
    private Hologram hologram;
    private int entityId;
    private Object entity;

    public NPC(@NotNull String id, @NotNull Location location) {
        this(id, location, null, null, null, null);
    }

    public NPC(@NotNull String id, @NotNull Location location, @Nullable String texture, @Nullable String signature) {
        this(id, location, texture, signature, null, null);
    }

    public NPC(@NotNull String id, @NotNull Location location, @Nullable Callable<List<String>> lines) {
        this(id, location, null, lines);
    }

    public NPC(@NotNull String id, @NotNull Location location, @Nullable Callable<List<String>> lines,@Nullable NPCAction action) {
        this(id, location, null, lines, action);
    }

    public NPC(@NotNull String id, @NotNull Location location, @Nullable OfflinePlayer skin, @Nullable Callable<List<String>> lines) {
        this(id, location, skin, lines, null);
    }

    public NPC(@NotNull String id, @NotNull Location location, @Nullable OfflinePlayer skin, @Nullable Callable<List<String>> lines, @Nullable NPCAction action) {
        this.id = id;

        if (skin != null) {
            UUID uuid = skin.getUniqueId();
            JsonObject profile = PlayerUtil.getProfileSigned(uuid);

            JsonObject textures = profile.getAsJsonArray("properties").get(0).getAsJsonObject();
            this.texture = textures.get("value").getAsString();
            this.signature = textures.get("signature").getAsString();
        }

        this.location = location;
        this.action = action;

        GameProfile profile = new GameProfile(UUID.randomUUID(), id);
        if (texture != null && signature != null) {
            Property property = new Property("textures", texture, signature);
            profile.getProperties().put("textures", property);
        }

        this.profile = profile;
        this.hologram = new Hologram(id, location, lines);

        this.entity = NMSUtil.createEntityPlayer(NMSUtil.getMCServer(), NMSUtil.getWorldServer(location.getWorld()), profile, location);
        if (entity == null) throw new RuntimeException("Failed to create NPC entity");

        try {
            this.entityId = (int) entity.getClass().getDeclaredMethod("getId").invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NPC(@NotNull String id, @NotNull Location location, @Nullable String texture, @Nullable String signature, @Nullable Callable<List<String>> lines, @Nullable NPCAction action) {
        this.id = id;
        this.location = location;
        this.texture = texture;
        this.signature = signature;
        this.action = action;

        GameProfile profile = new GameProfile(UUID.randomUUID(), id);
        if (texture != null && signature != null) {
            Property property = new Property("textures", texture, signature);
            profile.getProperties().put("textures", property);
        }

        this.profile = profile;
        this.hologram = new Hologram(id, location, lines);

        this.entity = NMSUtil.createEntityPlayer(NMSUtil.getMCServer(), NMSUtil.getWorldServer(location.getWorld()), profile, location);
        if (entity == null) throw new RuntimeException("Failed to create NPC entity");

        try {
            this.entityId = (int) entity.getClass().getDeclaredMethod("getId").invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;


    }

    public void spawn() {
        Preconditions.checkState(entity == null, "NPC is already spawned");
        npcs.add(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawn(player);
        }

        TEAM.addEntry(id);
        this.hologram.spawn();
    }

    public void spawn(Player player) {
        ProtocolManager manager = nitro.getProtocolManager();
        if (manager == null) return;

        PacketContainer spawnPacket = manager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        PacketContainer prespawnPacket = manager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        PacketContainer removePacket = manager.createPacket(PacketType.Play.Server.PLAYER_INFO);

        spawnPacket.getIntegers().write(0, entityId);
        spawnPacket.getUUIDs().write(0, profile.getId());
        spawnPacket.getDoubles().write(0, location.getX());
        spawnPacket.getDoubles().write(1, location.getY());
        spawnPacket.getDoubles().write(2, location.getZ());
        spawnPacket.getBytes().write(0, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        spawnPacket.getBytes().write(1, (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));

        prespawnPacket.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        prespawnPacket.getPlayerInfoDataLists().write(0, new ArrayList<>());

        removePacket.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        removePacket.getPlayerInfoDataLists().write(0, new ArrayList<>());

        try {
            manager.sendServerPacket(player, prespawnPacket);
            manager.sendServerPacket(player, spawnPacket);
            manager.sendServerPacket(player, removePacket);
        } catch (Exception e) {
            LogUtil.handleException("Failed to spawn NPC for " + player.getName(), e);
        }
    }

    public void despawn() {
        Preconditions.checkState(entity != null, "NPC is not spawned");
        npcs.remove(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            despawn(player);
        }

        TEAM.removeEntry(id);
        this.hologram.despawn();
    }

    public void despawn(Player player) {
        ProtocolManager manager = nitro.getProtocolManager();
        if (manager == null) return;

        PacketContainer despawnPacket = manager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        despawnPacket.getIntegerArrays().write(0, new int[] {entityId});

        try {
            manager.sendServerPacket(player, despawnPacket);
        } catch (Exception e) {
            LogUtil.handleException("Failed to despawn NPC for " + player.getName(), e);
        }
    }

    @FunctionalInterface
    public interface NPCAction {
        void run(Player player, NPC npc, ClickType type);
    }
}
