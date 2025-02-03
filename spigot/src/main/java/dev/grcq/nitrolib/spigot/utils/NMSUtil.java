package dev.grcq.nitrolib.spigot.utils;

import com.mojang.authlib.GameProfile;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@UtilityClass
public class NMSUtil {

    private static final String NMS_VERSION;

    private static final Class<?> CRAFT_PLAYER_CLASS;
    private static final Class<?> ENTITY_PLAYER_CLASS;
    private static final Class<?> MINECRAFT_SERVER_CLASS;
    private static final Class<?> WORLD_SERVER_CLASS;
    private static final Class<?> CRAFT_SERVER_CLASS;
    private static final Class<?> CRAFT_WORLD_CLASS;
    private static final Class<?> GAME_PROFILE_CLASS;
    private static final Class<?> PACKET_CLASS;
    private static final Class<?> PLAYER_INTERACT_MANAGER_CLASS;
    private static final Class<?> CRAFT_INVENTORY_CLASS;
    private static final Class<?> MINECRAFT_INVENTORY_CLASS;
    private static final Class<?> PACKET_OUT_OPEN_WINDOW_CLASS;
    private static final Class<?> ICHAT_BASE_COMPONENT_CLASS;
    private static final Class<?> CHAT_COMPONENT_TEXT_CLASS;

    private static final Field RECENT_TPS_FIELD;
    private static final Field IINVENTORY_FIELD;
    private static final Field TITLE_FIELD;
    private static final Field CONTAINER_COUNTER_FIELD;
    private static final Constructor<?> OPEN_WINDOW_PACKET_CONSTRUCTOR;
    private static final boolean OPEN_WINDOW_PACKET_CONSTRUCTOR_NEW;

    static {
        NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        CRAFT_PLAYER_CLASS = getOBCClass("entity.CraftPlayer");
        ENTITY_PLAYER_CLASS = getNMSClass("EntityPlayer");
        MINECRAFT_SERVER_CLASS = getNMSClass("MinecraftServer");
        WORLD_SERVER_CLASS = getNMSClass("WorldServer");
        CRAFT_SERVER_CLASS = getOBCClass("CraftServer");
        CRAFT_WORLD_CLASS = getOBCClass("CraftWorld");
        GAME_PROFILE_CLASS = getNMSClass("GameProfile");
        PACKET_CLASS = getNMSClass("Packet");
        PLAYER_INTERACT_MANAGER_CLASS = getNMSClass("PlayerInteractManager");
        CRAFT_INVENTORY_CLASS = getOBCClass("inventory.CraftInventory");
        MINECRAFT_INVENTORY_CLASS = getOBCClass("inventory.CraftInventoryCustom$MinecraftInventory");
        PACKET_OUT_OPEN_WINDOW_CLASS = getNMSClass("PacketPlayOutOpenWindow");
        ICHAT_BASE_COMPONENT_CLASS = getNMSClass("IChatBaseComponent");
        CHAT_COMPONENT_TEXT_CLASS = getNMSClass("ChatComponentText");

        Field recentTpsField = null;
        Field iInventoryField = null;
        Field titleField = null;
        Field containerCounterField = null;
        Constructor<?> openWindowPacketConstructor = null;
        boolean openWindowPacketConstructorNew = false;

        try {
            recentTpsField = MINECRAFT_SERVER_CLASS.getDeclaredField("recentTps");
            iInventoryField = CRAFT_INVENTORY_CLASS.getDeclaredField("inventory");
            titleField = MINECRAFT_INVENTORY_CLASS.getDeclaredField("title");
            containerCounterField = ENTITY_PLAYER_CLASS.getDeclaredField("containerCounter");
            try {
                openWindowPacketConstructor = PACKET_OUT_OPEN_WINDOW_CLASS.getDeclaredConstructor(int.class, int.class, String.class, int.class, boolean.class);
                openWindowPacketConstructorNew = true;
            } catch (NoSuchMethodException e) {
                openWindowPacketConstructor = PACKET_OUT_OPEN_WINDOW_CLASS.getDeclaredConstructor(int.class, String.class, ICHAT_BASE_COMPONENT_CLASS, int.class);
            }

            recentTpsField.setAccessible(true);
            iInventoryField.setAccessible(true);
            titleField.setAccessible(true);
            containerCounterField.setAccessible(true);
            openWindowPacketConstructor.setAccessible(true);
        } catch (Exception e) {
            LogUtil.handleException(e);
        }

        RECENT_TPS_FIELD = recentTpsField;
        IINVENTORY_FIELD = iInventoryField;
        TITLE_FIELD = titleField;
        CONTAINER_COUNTER_FIELD = containerCounterField;
        OPEN_WINDOW_PACKET_CONSTRUCTOR = openWindowPacketConstructor;
        OPEN_WINDOW_PACKET_CONSTRUCTOR_NEW = openWindowPacketConstructorNew;
    }

    public static String getNMSVersion() {
        return NMS_VERSION;
    }

    @Nullable
    public static Class<?> getOBCClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + "." + name);
        } catch (ClassNotFoundException e) {
            LogUtil.verbose("Failed to get OBC class " + name);
            return null;
        }
    }

    @Nullable
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + NMS_VERSION + "." + name);
        } catch (ClassNotFoundException e) {
            LogUtil.verbose("Failed to get NMS class " + name);
            return null;
        }
    }

    public static void sendPacket(Object packet, Object player) {
        try {
            Object craftPlayer = CRAFT_PLAYER_CLASS.cast(player);
            Object handle = CRAFT_PLAYER_CLASS.getMethod("getHandle").invoke(craftPlayer);

            Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass().getMethod("sendPacket", PACKET_CLASS).invoke(connection, packet);
        } catch (Exception e) {
            LogUtil.handleException("Failed to send packet", e);
        }
    }

    public static Object getMCServer() {
        try {
            return MINECRAFT_SERVER_CLASS.getMethod("getServer").invoke(null);
        } catch (Exception e) {
            LogUtil.handleException("Failed to get MC server", e);
            return null;
        }
    }

    public static Object getCraftServer() {
        try {
            Object craftServer = CRAFT_SERVER_CLASS.cast(Bukkit.getServer());
            return CRAFT_PLAYER_CLASS.getMethod("getServer").invoke(craftServer);
        } catch (Exception e) {
            LogUtil.handleException("Failed to get craft server", e);
            return null;
        }
    }

    public static Object getWorldServer(Object world) {
        try {
            Object craftWorld = CRAFT_WORLD_CLASS.cast(world);
            return CRAFT_WORLD_CLASS.getMethod("getHandle").invoke(craftWorld);
        } catch (Exception e) {
            LogUtil.handleException("Failed to get world server", e);
            return null;
        }
    }

    public static float[] getTicksPerSecond() {
        try {
            Object server = getMCServer();
            double[] tps = (double[]) RECENT_TPS_FIELD.get(server);

            return new float[] {(float) Math.round(tps[0] * 100) / 100, (float) Math.round(tps[1] * 100) / 100, (float) Math.round(tps[2] * 100) / 100};
        } catch (Exception e) {
            LogUtil.handleException("Failed to get TPS", e);
            return new float[] {0f, 0f, 0f};
        }
    }

    public static Object createEntityPlayer(Object world, String name) {
        try {
            Object worldServer = WORLD_SERVER_CLASS.cast(world);

            return ENTITY_PLAYER_CLASS.getConstructor(WORLD_SERVER_CLASS, GAME_PROFILE_CLASS).newInstance(worldServer, new com.mojang.authlib.GameProfile(null, name));
        } catch (Exception e) {
            LogUtil.handleException("Failed to create entity player", e);
            return null;
        }
    }

    public static Object getWorldServer(World world) {
        try {
            Object craftWorld = CRAFT_WORLD_CLASS.cast(world);
            return CRAFT_WORLD_CLASS.getDeclaredMethod("getHandle").invoke(craftWorld);
        } catch (Exception e) {
            LogUtil.handleException("Failed to get world server", e);
            return null;
        }
    }

    public static Object createEntityPlayer(Object mcServer, Object worldServer, GameProfile gameProfile, Location location) {
        try {
            Object interactManager = PLAYER_INTERACT_MANAGER_CLASS.getDeclaredConstructors()[0]
                    .newInstance(worldServer);

            Object entityPlayer = ENTITY_PLAYER_CLASS.getDeclaredConstructor(
                    MINECRAFT_SERVER_CLASS,
                    WORLD_SERVER_CLASS,
                    GameProfile.class,
                    PLAYER_INTERACT_MANAGER_CLASS
            ).newInstance(mcServer, worldServer, gameProfile, interactManager);

            ENTITY_PLAYER_CLASS
                    .getMethod("setLocation", double.class, double.class, double.class, float.class, float.class)
                    .invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            return entityPlayer;
        } catch (Exception e) {
            LogUtil.handleException("Failed to create entity player", e);
            return null;
        }
    }

    public static Object getEntityPlayer(Player player) {
        try {
            Object craftPlayer = CRAFT_PLAYER_CLASS.cast(player);
            return CRAFT_PLAYER_CLASS.getDeclaredMethod("getHandle").invoke(craftPlayer);
        } catch (Exception e) {
            LogUtil.handleException("Failed to get entity player", e);
            return null;
        }
    }

    public static void setInventoryTitle(Player player, Inventory inventory, String newTitle) {
        try {
            Object iInventory = IINVENTORY_FIELD.get(inventory);
            TITLE_FIELD.set(iInventory, newTitle);

            Object handle = getEntityPlayer(player);
            Integer containerCounter = (Integer) CONTAINER_COUNTER_FIELD.get(handle);

            Object chatComponent = CHAT_COMPONENT_TEXT_CLASS.getConstructor(String.class).newInstance(newTitle);

            Object packet = OPEN_WINDOW_PACKET_CONSTRUCTOR_NEW
                    ? OPEN_WINDOW_PACKET_CONSTRUCTOR.newInstance(containerCounter, 0, newTitle, inventory.getSize(), true)
                    : OPEN_WINDOW_PACKET_CONSTRUCTOR.newInstance(containerCounter, newTitle, chatComponent, inventory.getSize());
            sendPacket(packet, player);
        } catch (Exception e) {
            LogUtil.handleException("Failed to set inventory title", e);
        }
    }

}
